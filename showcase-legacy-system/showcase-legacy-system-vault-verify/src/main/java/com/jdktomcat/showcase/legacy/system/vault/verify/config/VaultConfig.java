package com.jdktomcat.showcase.legacy.system.vault.verify.config;

import com.jdktomcat.showcase.legacy.system.vault.verify.listener.MultiDbLeaseListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.lease.SecretLeaseContainer;

/**
 * vault 相关配置
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.vault.enabled", havingValue = "true")
public class VaultConfig {

    @Autowired
    private MultiDbLeaseListener multiDbLeaseListener;

    @Value("${spring.cloud.vault.databases.typayv2slave.role}")
    private String typayv2slaveRole;

    @Bean
    public TaskScheduler vaultTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("vault-lease-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean(destroyMethod = "destroy")
    public SecretLeaseContainer leaseContainer(VaultOperations operations, TaskScheduler vaultTaskScheduler) {
        SecretLeaseContainer container = new SecretLeaseContainer(operations, vaultTaskScheduler);
        container.addLeaseListener(multiDbLeaseListener);
        // 必须添加 RequestedSecret，否则容器不会向 Vault 请求凭证，也就不会触发任何事件
        container.requestRotatingSecret("database/creds/" + typayv2slaveRole);
        container.afterPropertiesSet();
        container.start();
        return container;
    }
}