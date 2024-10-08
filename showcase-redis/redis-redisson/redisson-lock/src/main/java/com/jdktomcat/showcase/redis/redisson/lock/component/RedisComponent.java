package com.jdktomcat.showcase.redis.redisson.lock.component;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisComponent {

    @Resource
    private RedissonClient redissonClient;

    public RLock getLock(String key) throws InterruptedException {
        RLock lock = redissonClient.getLock(key);
        boolean result = lock.tryLock(10, TimeUnit.SECONDS);
        log.info("lock:{}", result);
        return lock;
    }

    public RLock getFairLock(String key) throws InterruptedException {
        RLock lock = redissonClient.getFairLock(key);
        boolean result = lock.tryLock(10, TimeUnit.SECONDS);
        log.info("fair lock:{}", result);
        return lock;
    }
}
