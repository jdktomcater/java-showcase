package com.jdktomcat.showcase.legacy.system.vault.hikaricp.config;

import com.jdktomcat.showcase.legacy.system.vault.hikaricp.listener.MultiDbLeaseListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.support.LeaseStrategy;

/**
 * vault 相关配置
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.vault.enabled", havingValue = "true")
public class VaultConfig {

    @Autowired
    private MultiDbLeaseListener multiDbLeaseListener;

    @Value("${spring.cloud.vault.database.role:${spring.cloud.vault.databases.typayv2slave.role:}}")
    private String typayv2slaveRole;

    @Bean(name = "vaultLeaseTaskScheduler")
    public TaskScheduler vaultLeaseTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("vault-lease-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean(destroyMethod = "destroy")
    public SecretLeaseContainer leaseContainer(VaultOperations operations,
                                               @Qualifier("vaultLeaseTaskScheduler") TaskScheduler vaultLeaseTaskScheduler) {
        if (typayv2slaveRole == null || typayv2slaveRole.trim().isEmpty()) {
            throw new IllegalStateException("Missing Vault database role configuration.");
        }
        SecretLeaseContainer container = new SecretLeaseContainer(operations, vaultLeaseTaskScheduler);
        container.addLeaseListener(multiDbLeaseListener);
        // 必须添加 RequestedSecret，否则容器不会向 Vault 请求凭证，也就不会触发任何事件
        container.requestRotatingSecret("database/creds/" + typayv2slaveRole);
        // 设置网络相关策略 网络/I/O 错误时保留租约并继续重试，网络恢复后自动恢复续期
        container.setLeaseStrategy(LeaseStrategy.retainOnError());
        container.afterPropertiesSet();
        container.start();
        return container;
    }
}