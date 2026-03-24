package com.jdktomcat.showcase.legacy.system.vault.hikaricp.listener;

import com.jdktomcat.showcase.legacy.system.vault.hikaricp.config.SwitchableDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.lease.event.LeaseListener;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;

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
public class MultiDbLeaseListener implements LeaseListener {

    @Autowired
    private SwitchableDataSource dataSourceWrapper;

    @Autowired(required = false)
    @Qualifier("threadPoolTaskExecutor")
    private Executor taskExecutor;

    @Value("${vault.datasource.close-check-interval-ms:5000}")
    private long closeCheckIntervalMs;

    @Value("${vault.datasource.close-warn-after-ms:10000}")
    private long closeWarnAfterMs;

    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);


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
        rotate(username, password);
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

    private HikariDataSource createReplacementDataSource(HikariDataSource old, String user, String pwd) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(old.getDriverClassName());
        config.setJdbcUrl(old.getJdbcUrl());
        config.setUsername(user);
        config.setPassword(pwd);
        config.setAutoCommit(old.isAutoCommit());
        config.setReadOnly(old.isReadOnly());
        config.setConnectionTimeout(old.getConnectionTimeout());
        config.setValidationTimeout(old.getValidationTimeout());
        config.setIdleTimeout(old.getIdleTimeout());
        config.setLeakDetectionThreshold(old.getLeakDetectionThreshold());
        config.setMaxLifetime(old.getMaxLifetime());
        config.setMaximumPoolSize(old.getMaximumPoolSize());
        config.setMinimumIdle(old.getMinimumIdle());
        config.setInitializationFailTimeout(old.getInitializationFailTimeout());
        config.setIsolateInternalQueries(old.isIsolateInternalQueries());
        config.setAllowPoolSuspension(old.isAllowPoolSuspension());
        config.setRegisterMbeans(old.isRegisterMbeans());
        config.setConnectionTestQuery(old.getConnectionTestQuery());
        config.setConnectionInitSql(old.getConnectionInitSql());
        config.setCatalog(old.getCatalog());
        config.setSchema(old.getSchema());
        config.setTransactionIsolation(old.getTransactionIsolation());
        config.setPoolName(buildRotatedPoolName(old.getPoolName()));
        Properties dataSourceProperties = new Properties();
        dataSourceProperties.putAll(old.getDataSourceProperties());
        config.setDataSourceProperties(dataSourceProperties);
        return new HikariDataSource(config);
    }

    private String buildRotatedPoolName(String oldPoolName) {
        String poolName = oldPoolName;
        if (poolName == null || poolName.trim().isEmpty()) {
            poolName = "vault-hikari";
        }
        return poolName + "-rotated-" + System.currentTimeMillis();
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
                log.warn("旧数据源:{}仍有活跃连接，延迟关闭。activeConnections={}, waitedMs={}",
                        old.getUsername(), poolBean.getActiveConnections(), waitedMs);
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

    @PreDestroy
    public void onShutdown() {
        shuttingDown.set(true);
        log.info("触发优雅下线:MultiDbLeaseListener 开始关闭，不再处理新的Vault租约事件。");
    }
}
