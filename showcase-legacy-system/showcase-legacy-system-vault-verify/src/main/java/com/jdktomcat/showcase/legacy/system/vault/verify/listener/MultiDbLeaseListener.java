package com.jdktomcat.showcase.legacy.system.vault.verify.listener;

import com.alibaba.druid.pool.DruidDataSource;
import com.jdktomcat.showcase.legacy.system.vault.verify.config.SwitchableDataSource;
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
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

/**
 * 多数据源监听器
 */
@Slf4j
@Component
public class MultiDbLeaseListener implements LeaseListener {

    @Autowired
    private SwitchableDataSource slaveTypayv2DataSourceWrapper;

    @Autowired(required = false)
    @Qualifier("threadPoolTaskExecutor")
    private Executor taskExecutor;

    @Value("${vault.datasource.close-check-interval-ms:5000}")
    private long closeCheckIntervalMs;

    @Value("${vault.datasource.close-warn-after-ms:300000}")
    private long closeWarnAfterMs;

    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void onLeaseEvent(@NonNull SecretLeaseEvent event) {
        // TODO 如何兜底vault服务器异常场景（随机返回）
        if (shuttingDown.get()) {
            log.info("应用正在关闭，忽略Vault租约事件，type={}", event.getClass().getSimpleName());
            return;
        }
        if(atomicInteger.incrementAndGet() % 3 == 0){
            log.warn("人为跳过更新事件！");
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
        DruidDataSource old = slaveTypayv2DataSourceWrapper.getCurrent();
        if (Objects.equals(old.getUsername(), user) && Objects.equals(old.getPassword(), pwd)) {
            log.warn("数据库连接用户名及密码未发生变化，不做切换！");
            return;
        }
        try {
            DruidDataSource newDs = old.cloneDruidDataSource();
            newDs.setUsername(user);
            newDs.setPassword(pwd);
            newDs.init();
            validateConnection(newDs);
            slaveTypayv2DataSourceWrapper.switchTo(newDs);
            log.info("Vault数据库凭证轮换成功，已切换到新连接池。");
        } catch (SQLException sqlException) {
            log.error("切换数据源异常！", sqlException);
            return;
        }
        // 异步关闭旧数据源，避免 setEnable(false) 导致健康检查等仍引用旧数据源时抛出 DataSourceDisableException
        log.info("旧数据源：{}，即将被回收！。", old.getUsername());
        safeCloseAsync(old);
    }

    /**
     * 校验数据源
     *
     * @param dataSource 数据源
     * @throws SQLException SQL异常
     */
    private void validateConnection(DruidDataSource dataSource) throws SQLException {
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
    private void safeCloseAsync(DruidDataSource old) {
        Runnable task = () -> {
            long waitedMs = 0;
            boolean warned = false;
            while (!shuttingDown.get() && old.getActiveCount() > 0) {
                try {
                    sleep(closeCheckIntervalMs);
                    waitedMs += closeCheckIntervalMs;
                    if (!warned && waitedMs >= closeWarnAfterMs) {
                        warned = true;
                        log.warn("旧数据源仍有活跃连接，延迟关闭。activeCount={}, waitedMs={}", old.getActiveCount(), waitedMs);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("旧数据源关闭等待被中断，activeCount={}", old.getActiveCount());
                    return;
                }
            }
            log.info("旧数据源：{}活跃连接已清零，开始关闭连接池。", old.getUsername());
            old.close();
        };
        if (taskExecutor != null) {
            taskExecutor.execute(task);
        } else {
            Thread closeThread = new Thread(task, "vault-ds-close");
            closeThread.setDaemon(true);
            closeThread.start();
        }
    }

    @PreDestroy
    public void onShutdown() {
        shuttingDown.set(true);
        log.info("触发优雅下线:MultiDbLeaseListener 开始关闭，不再处理新的Vault租约事件。");
    }
}
