package com.jdktomcat.showcase.redis.redisson.lock.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * redisson配置类
 * @author Tracy  R
 * @date 2019/12/19
 */
@EnableConfigurationProperties({RedissonProperties.class})
@RequiredArgsConstructor
@Configuration
public class RedissonConfig {

    private final RedisProperties redisProperties;

    private final RedissonProperties redissonProperties;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Method clusterMethod = ReflectionUtils.findMethod(RedisProperties.class, "getCluster");
        Method timeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getTimeout");
        Object timeoutValue = ReflectionUtils.invokeMethod(timeoutMethod, redisProperties);
        int timeout;
        if (null == timeoutValue) {
            timeout = 0;
        } else if (!(timeoutValue instanceof Integer)) {
            Method millisMethod = ReflectionUtils.findMethod(timeoutValue.getClass(), "toMillis");
            timeout = ((Long) ReflectionUtils.invokeMethod(millisMethod, timeoutValue)).intValue();
        } else {
            timeout = (Integer) timeoutValue;
        }

        Object clusterObject = ReflectionUtils.invokeMethod(clusterMethod, redisProperties);
        Method nodesMethod = ReflectionUtils.findMethod(clusterObject.getClass(), "getNodes");
        List<String> nodesObject = (List) ReflectionUtils.invokeMethod(nodesMethod, clusterObject);

        String[] nodes = convert(nodesObject);

        Config config = new Config();
        config.setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                .setLockWatchdogTimeout(redissonProperties.getLockWatchdogTimeout())
                .useClusterServers()
                .addNodeAddress(nodes)
                .setScanInterval(redissonProperties.getScanInterval())
                .setReadMode(ReadMode.valueOf(redissonProperties.getReadMode()))
                .setSlaveConnectionMinimumIdleSize(redissonProperties.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(redissonProperties.getSlaveConnectionPoolSize())
                .setFailedSlaveReconnectionInterval(redissonProperties.getFailedSlaveReconnectionInterval())
                .setFailedSlaveCheckInterval(redissonProperties.getFailedSlaveCheckInterval())
                .setTimeout(redissonProperties.getTimeout())
                .setPingConnectionInterval(redissonProperties.getPingTimeout())
                .setIdleConnectionTimeout(redissonProperties.getIdleConnectionTimeout())
                .setRetryAttempts(redissonProperties.getRetryAttempts())
                .setRetryInterval(redissonProperties.getRetryInterval())
                .setConnectTimeout(timeout)
                .setPassword(redisProperties.getPassword())
                .setConnectTimeout(redissonProperties.getConnectTimeout())
                .setMasterConnectionMinimumIdleSize(redissonProperties.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(redissonProperties.getMasterConnectionPoolSize());

        return Redisson.create(config);
    }

    private String[] convert(List<String> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        for (String node : nodesObject) {
            if (!node.startsWith("redis://") && !node.startsWith("rediss://")) {
                nodes.add("redis://" + node);
            } else {
                nodes.add(node);
            }
        }
        return nodes.toArray(new String[nodes.size()]);
    }
}
