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
import org.springframework.vault.core.lease.event.AfterSecretLeaseRenewedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;
import org.springframework.vault.core.lease.event.SecretLeaseExpiredEvent;

import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;
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

    @Value("${spring.datasource.username:}")
    private String fallbackUsername;

    @Value("${spring.datasource.password:}")
    private String fallbackPassword;

    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    private final AtomicBoolean renewalFailed = new AtomicBoolean(false);

    private boolean fallbackActive;

    private String lastVaultUsername;

    private String lastVaultPassword;


    @Override
    public void onLeaseEvent(@NonNull SecretLeaseEvent event) {
        if (shuttingDown.get()) {
            log.info("应用正在关闭，忽略Vault租约事件，type={}", event.getClass().getSimpleName());
            return;
        }
        if (event instanceof SecretLeaseExpiredEvent) {
            log.error("Vault租约已过期，切换到备份数据库账号，path={}", event.getSource().getPath());
            switchToFallbackDataSource();
            return;
        }
        if (event instanceof AfterSecretLeaseRenewedEvent) {
            if (renewalFailed.getAndSet(false)) {
                log.warn("忽略续租异常后产生的AfterSecretLeaseRenewedEvent，path={}", event.getSource().getPath());
                return;
            }
            switchBackToVaultAfterRenewal(event);
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
        log.info("收到Vault数据库凭证，path={}, username={}", path, username);
        synchronized (this) {
            lastVaultUsername = username;
            lastVaultPassword = password;
            if (rotate(username, password)) {
                fallbackActive = false;
            }
        }
    }

    private synchronized void switchBackToVaultAfterRenewal(SecretLeaseEvent event) {
        if (!fallbackActive || !hasText(lastVaultUsername) || !hasText(lastVaultPassword)) {
            return;
        }
        log.info("Vault租约续期恢复，尝试回切Vault数据库凭证，path={}", event.getSource().getPath());
        if (rotate(lastVaultUsername, lastVaultPassword)) {
            fallbackActive = false;
        }
    }

    /**
     * 转换
     *
     * @param user 新用户名
     * @param pwd  新密码
     */
    private synchronized boolean rotate(String user, String pwd) {
        HikariDataSource old = dataSourceWrapper.getCurrent();
        String loginUsername = oceanBaseUsernameFormatter.buildOceanBaseUsername(user);
        if (Objects.equals(old.getUsername(), loginUsername) && Objects.equals(old.getPassword(), pwd)) {
            log.warn("数据库连接用户名及密码未发生变化，不做切换！");
            return true;
        }
        HikariDataSource newDs = null;
        try {
            newDs = createReplacementDataSource(old, loginUsername, pwd);
            validateConnection(newDs);
            dataSourceWrapper.switchTo(newDs);
            log.info("Vault数据库凭证轮换成功，已切换到新连接池。");
        } catch (Exception exception) {
            closeQuietly(newDs);
            log.error("切换数据源异常！", exception);
            return false;
        }
        // 异步关闭旧数据源，避免仍持有旧连接的请求立即失败。
        log.info("旧数据源：{}，即将被回收！。", old.getUsername());
        safeCloseAsync(old);
        return true;
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
        HikariConfig config = new HikariConfig();
        String jdbcUrl = firstNonBlank(old.getJdbcUrl(), old.getDataSourceProperties().getProperty("jdbcUrl"),
                old.getDataSourceProperties().getProperty("url"), configuredJdbcUrl);
        String driverClassName = firstNonBlank(old.getDriverClassName(), configuredDriverClassName);
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
    public void onLeaseError(@NonNull SecretLeaseEvent leaseEvent, @NonNull Exception exception) {
        if (shuttingDown.get()) {
            return;
        }
        renewalFailed.set(true);
        log.error("Vault租约获取或续期失败，立即切换到备份数据库账号，path={}", leaseEvent.getSource().getPath(), exception);
        switchToFallbackDataSource();
    }

    private synchronized void switchToFallbackDataSource() {
        if (shuttingDown.get() || fallbackActive) {
            return;
        }
        if (!hasText(fallbackUsername) || !hasText(fallbackPassword)) {
            log.error("Vault不可用，但未配置备份数据库账号密码，继续使用当前数据源。" + "请配置spring.datasource.username/password。");
            return;
        }
        HikariDataSource old = dataSourceWrapper.getCurrent();
        String loginUsername = oceanBaseUsernameFormatter.buildOceanBaseUsername(fallbackUsername);
        if (Objects.equals(old.getUsername(), loginUsername) && Objects.equals(old.getPassword(), fallbackPassword)) {
            fallbackActive = true;
            log.warn("当前连接池已使用备份数据库账号，无需重复切换。");
            return;
        }
        HikariDataSource fallbackDataSource = null;
        try {
            fallbackDataSource = createReplacementDataSource(old, loginUsername, fallbackPassword);
            validateConnection(fallbackDataSource);
            dataSourceWrapper.switchTo(fallbackDataSource);
            fallbackActive = true;
            log.warn("Vault不可用，已切换到spring.datasource配置的备份数据库账号。");
            safeCloseAsync(old);
        } catch (Exception exception) {
            closeQuietly(fallbackDataSource);
            log.error("Vault不可用，切换备份数据源失败，继续使用当前数据源。", exception);
        }
    }

    @PreDestroy
    public void onShutdown() {
        shuttingDown.set(true);
        log.info("触发优雅下线:MultiDbLeaseListener 开始关闭，不再处理新的Vault租约事件。");
    }
}
