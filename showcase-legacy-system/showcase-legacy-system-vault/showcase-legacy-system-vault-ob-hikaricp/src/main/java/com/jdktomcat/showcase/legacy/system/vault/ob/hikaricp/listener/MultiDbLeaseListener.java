package com.jdktomcat.showcase.legacy.system.vault.ob.hikaricp.listener;

import com.jdktomcat.showcase.legacy.system.vault.ob.hikaricp.config.OceanBaseUsernameFormatter;
import com.jdktomcat.showcase.legacy.system.vault.ob.hikaricp.config.SwitchableDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.lease.event.LeaseErrorListener;
import org.springframework.vault.core.lease.event.LeaseListener;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;

import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

/**
 * 多数据源监听器
 */
@Slf4j
@Component
public class MultiDbLeaseListener implements LeaseListener, LeaseErrorListener {

    @Autowired
    private SwitchableDataSource dataSourceWrapper;

    @Autowired
    private OceanBaseUsernameFormatter oceanBaseUsernameFormatter;

    @Autowired(required = false)
    @Qualifier("threadPoolTaskExecutor")
    private Executor taskExecutor;

    @Value("${vault.datasource.close-check-interval-ms:5000}")
    private long closeCheckIntervalMs;

    @Value("${vault.datasource.close-warn-after-ms:10000}")
    private long closeWarnAfterMs;

    @Value("${spring.datasource.url:${spring.datasource.hikari.jdbc-url:}}")
    private String configuredJdbcUrl;

    @Value("${spring.datasource.driver-class-name:${spring.datasource.driverClassName:}}")
    private String configuredDriverClassName;

    @Value("${vault.datasource.fallback.enabled:${VAULT_DATASOURCE_FALLBACK_ENABLED:true}}")
    private boolean fallbackEnabled;

    @Value("${vault.datasource.fallback-after-ms:${VAULT_DATASOURCE_FALLBACK_AFTER_MS:172800000}}")
    private long fallbackAfterMs;

    @Value("${vault.datasource.fallback.jdbc-url:${VAULT_DATASOURCE_FALLBACK_JDBC_URL:}}")
    private String fallbackJdbcUrl;

    @Value("${vault.datasource.fallback.driver-class-name:${VAULT_DATASOURCE_FALLBACK_DRIVER_CLASS_NAME:}}")
    private String fallbackDriverClassName;

    @Value("${vault.datasource.fallback.username:${VAULT_DATASOURCE_FALLBACK_USERNAME:}}")
    private String fallbackUsername;

    @Value("${vault.datasource.fallback.password:${VAULT_DATASOURCE_FALLBACK_PASSWORD:}}")
    private String fallbackPassword;

    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
    private final ScheduledExecutorService fallbackScheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "vault-ds-fallback");
        thread.setDaemon(true);
        return thread;
    });

    private ScheduledFuture<?> fallbackTask;

    private boolean fallbackActive;


    @Override
    public void onLeaseEvent(@NonNull SecretLeaseEvent event) {
        if (shuttingDown.get()) {
            log.info("应用正在关闭，忽略Vault租约事件，type={}", event.getClass().getSimpleName());
            return;
        }
        if (!(event instanceof SecretLeaseCreatedEvent)) {
            log.info("收到Vault租约事件，类型={}, source={}", event.getClass().getSimpleName(), event.getSource());
            return;
        }
        SecretLeaseCreatedEvent secretLeaseCreatedEvent = (SecretLeaseCreatedEvent) event;
        String path = secretLeaseCreatedEvent.getSource().getPath();
        Map<String, Object> data = secretLeaseCreatedEvent.getSecrets();
        String username = (String) data.get("username");
        String password = (String) data.get("password");
        log.info("发生相关事件路径：{} username:{} password:{}", path, username, password);
        synchronized (this) {
            cancelFallbackTask();
            fallbackActive = false;
            rotate(username, password);
        }
    }

    /**
     * 转换
     *
     * @param user 新用户名
     * @param pwd  新密码
     */
    private synchronized void rotate(String user, String pwd) {
        HikariDataSource old = dataSourceWrapper.getCurrent();
        if (Objects.equals(old.getUsername(), user) && Objects.equals(old.getPassword(), pwd)) {
            log.warn("数据库连接用户名及密码未发生变化，不做切换！");
            return;
        }
        HikariDataSource newDs = null;
        try {
            newDs = createReplacementDataSource(old, user, pwd);
            validateConnection(newDs);
            dataSourceWrapper.switchTo(newDs);
            log.info("Vault数据库凭证轮换成功，已切换到新连接池。");
        } catch (SQLException sqlException) {
            closeQuietly(newDs);
            log.error("切换数据源异常！", sqlException);
            return;
        }
        // 异步关闭旧数据源，避免仍持有旧连接的请求立即失败。
        log.info("旧数据源：{}，即将被回收！。", old.getUsername());
        safeCloseAsync(old);
    }

    /**
     * 创建新的数据源
     *
     * @param old  原先数据源
     * @param user 新用户名
     * @param pwd  新密码
     * @return 新的数据源
     */
    private HikariDataSource createReplacementDataSource(HikariDataSource old, String user, String pwd) {
        return createReplacementDataSource(old, user, pwd, null, null);
    }

    private HikariDataSource createReplacementDataSource(HikariDataSource old, String user, String pwd, String jdbcUrlOverride, String driverClassNameOverride) {
        HikariConfig config = new HikariConfig();
        String jdbcUrl = firstNonBlank(jdbcUrlOverride, old.getJdbcUrl(), old.getDataSourceProperties().getProperty("jdbcUrl"), old.getDataSourceProperties().getProperty("url"), configuredJdbcUrl);
        String driverClassName = firstNonBlank(driverClassNameOverride, old.getDriverClassName(), configuredDriverClassName);
        if (hasText(driverClassName)) {
            config.setDriverClassName(driverClassName);
        }
        if (!hasText(jdbcUrl)) {
            throw new IllegalStateException("Unable to determine jdbcUrl for rotated HikariDataSource.");
        }
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(oceanBaseUsernameFormatter.buildOceanBaseUsername(user));
        config.setPassword(pwd);
        config.setAutoCommit(old.isAutoCommit());
        config.setReadOnly(old.isReadOnly());
        if (old.getConnectionTimeout() > 0) {
            config.setConnectionTimeout(old.getConnectionTimeout());
        }
        if (old.getValidationTimeout() > 0) {
            config.setValidationTimeout(old.getValidationTimeout());
        }
        if (old.getIdleTimeout() >= 0) {
            config.setIdleTimeout(old.getIdleTimeout());
        }
        if (old.getLeakDetectionThreshold() >= 0) {
            config.setLeakDetectionThreshold(old.getLeakDetectionThreshold());
        }
        if (old.getMaxLifetime() > 0) {
            config.setMaxLifetime(old.getMaxLifetime());
        }
        if (old.getMaximumPoolSize() > 0) {
            config.setMaximumPoolSize(old.getMaximumPoolSize());
        }
        if (old.getMinimumIdle() >= 0) {
            config.setMinimumIdle(old.getMinimumIdle());
        }
        config.setInitializationFailTimeout(old.getInitializationFailTimeout());
        config.setIsolateInternalQueries(old.isIsolateInternalQueries());
        config.setAllowPoolSuspension(old.isAllowPoolSuspension());
        config.setRegisterMbeans(old.isRegisterMbeans());
        if (hasText(old.getConnectionTestQuery())) {
            config.setConnectionTestQuery(old.getConnectionTestQuery());
        }
        if (hasText(old.getConnectionInitSql())) {
            config.setConnectionInitSql(old.getConnectionInitSql());
        }
        if (hasText(old.getCatalog())) {
            config.setCatalog(old.getCatalog());
        }
        if (hasText(old.getSchema())) {
            config.setSchema(old.getSchema());
        }
        if (hasText(old.getTransactionIsolation())) {
            config.setTransactionIsolation(old.getTransactionIsolation());
        }
        config.setPoolName(buildRotatedPoolName());
        Properties dataSourceProperties = new Properties();
        dataSourceProperties.putAll(old.getDataSourceProperties());
        config.setDataSourceProperties(dataSourceProperties);
        return new HikariDataSource(config);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String firstNonBlank(String... candidates) {
        if (candidates == null) {
            return null;
        }
        for (String candidate : candidates) {
            if (hasText(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private String buildRotatedPoolName() {
        return "vault-hikari-rotated-" + System.currentTimeMillis();
    }

    /**
     * 校验数据源
     *
     * @param dataSource 数据源
     * @throws SQLException SQL异常
     */
    private void validateConnection(HikariDataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            if (!connection.isValid(2)) {
                throw new SQLException("新数据源连接校验失败");
            }
        }
    }

    /**
     * 安全优雅关闭原数据源
     *
     * @param old 原数据源
     */
    private void safeCloseAsync(HikariDataSource old) {
        Runnable task = () -> {
            try {
                waitForActiveConnectionsToDrain(old);
                old.close();
            } catch (Exception e) {
                if (e.getCause() instanceof javax.management.InstanceNotFoundException || e.getCause() != null && e.getCause().getClass().getName().contains("InstanceNotFoundException")) {
                    log.info("关闭旧数据源时JMX MBean已不存在，忽略。old={}", old.getUsername());
                } else {
                    log.warn("关闭旧数据源异常", e);
                }
            }
        };
        if (taskExecutor != null) {
            taskExecutor.execute(task);
        } else {
            Thread closeThread = new Thread(task, "vault-ds-close");
            closeThread.setDaemon(true);
            closeThread.start();
        }
    }

    private void waitForActiveConnectionsToDrain(HikariDataSource old) throws InterruptedException {
        long waitedMs = 0;
        boolean warned = false;
        while (!shuttingDown.get()) {
            HikariPoolMXBean poolBean = old.getHikariPoolMXBean();
            if (poolBean == null || poolBean.getActiveConnections() <= 0) {
                return;
            }
            sleep(closeCheckIntervalMs);
            waitedMs += closeCheckIntervalMs;
            if (!warned && waitedMs >= closeWarnAfterMs) {
                warned = true;
                log.warn("旧数据源:{}仍有活跃连接，延迟关闭。activeConnections={}, waitedMs={}", old.getUsername(), poolBean.getActiveConnections(), waitedMs);
            }
        }
    }

    private void closeQuietly(HikariDataSource dataSource) {
        if (dataSource == null || dataSource.isClosed()) {
            return;
        }
        try {
            dataSource.close();
        } catch (Exception e) {
            log.warn("关闭新建失败的数据源异常", e);
        }
    }

    @Override
    public void onLeaseError(SecretLeaseEvent leaseEvent, Exception exception) {
        if (shuttingDown.get() || !fallbackEnabled || fallbackActive) {
            return;
        }
        synchronized (this) {
            if (shuttingDown.get() || fallbackActive || fallbackTask != null && !fallbackTask.isDone()) {
                return;
            }
            if (fallbackAfterMs < 0) {
                log.warn("Vault租约持续失败，已禁用静态数据源降级：fallbackAfterMs={}", fallbackAfterMs);
                return;
            }
            fallbackTask = fallbackScheduler.schedule(this::switchToFallbackDataSource, fallbackAfterMs, TimeUnit.MILLISECONDS);
            leaseEvent.getSource();
            log.warn("Vault租约访问失败，将在{}毫秒后尝试切换静态数据源，path={}", fallbackAfterMs, leaseEvent.getSource().getPath(), exception);
        }
    }

    private synchronized void switchToFallbackDataSource() {
        fallbackTask = null;
        if (shuttingDown.get() || fallbackActive) {
            return;
        }
        if (!hasText(fallbackUsername) || !hasText(fallbackPassword)) {
            log.error("Vault长时间不可用，但未配置静态数据源账号密码，继续使用当前数据源。"
                    + "请配置vault.datasource.fallback.username/password。");
            return;
        }

        HikariDataSource old = dataSourceWrapper.getCurrent();
        HikariDataSource fallbackDataSource = null;
        try {
            fallbackDataSource = createReplacementDataSource(old, fallbackUsername, fallbackPassword, fallbackJdbcUrl, fallbackDriverClassName);
            validateConnection(fallbackDataSource);
            dataSourceWrapper.switchTo(fallbackDataSource);
            fallbackActive = true;
            log.error("Vault已持续不可用{}毫秒，已切换到静态数据源。", fallbackAfterMs);
            safeCloseAsync(old);
        } catch (Exception exception) {
            closeQuietly(fallbackDataSource);
            log.error("Vault长时间不可用，切换静态数据源失败，继续使用当前数据源。", exception);
        }
    }

    private synchronized void cancelFallbackTask() {
        if (fallbackTask != null) {
            fallbackTask.cancel(false);
            fallbackTask = null;
        }
    }

    @PreDestroy
    public void onShutdown() {
        shuttingDown.set(true);
        cancelFallbackTask();
        fallbackScheduler.shutdownNow();
        log.info("触发优雅下线:MultiDbLeaseListener 开始关闭，不再处理新的Vault租约事件。");
    }
}
