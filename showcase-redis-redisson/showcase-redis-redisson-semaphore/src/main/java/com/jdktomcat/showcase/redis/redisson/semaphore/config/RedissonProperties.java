package com.jdktomcat.showcase.redis.redisson.semaphore.config;

import lombok.Getter;
import lombok.Setter;
import org.redisson.config.ReadMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redisson配置类
 * @author Tracy
 * @date 2019/12/19
 */
@ConfigurationProperties(prefix = "spring.redis.redisson")
@Getter
@Setter
public class RedissonProperties {

    private int threads = 32;
    private int nettyThreads = 64;
    private long lockWatchdogTimeout = 30 * 1000;

    private int timeout = 3000;
    private int pingTimeout = 1000;
    private int idleConnectionTimeout = 10000;
    private int retryAttempts = 3;
    private int retryInterval = 1500;

    private int scanInterval = 5000;
    private String readMode = ReadMode.SLAVE.name();

    private int failedSlaveReconnectionInterval = 3000;
    private int failedSlaveCheckInterval = 180000;

    private int connectTimeout = 10000;

    private int masterConnectionMinimumIdleSize = 5;
    private int masterConnectionPoolSize = 64;

    private int slaveConnectionMinimumIdleSize = 5;
    private int slaveConnectionPoolSize = 64;

    private int subscriptionConnectionPoolSize = 5000;

    private int subscriptionsPerConnection = 5000;


}
