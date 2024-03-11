package com.jdktomcat.demo.redis.template.component;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：redis组件
 * @date 2023/11/8
 */
@Slf4j
@Component
public class RedisComponent {

    @Autowired
    private RedissonClient redissonClient;

    public boolean tryLock(String lockKey, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("redis Exception: {}", e.getMessage(), e);
            return false;
        }
    }

    public void unlock(String lockKey) {
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        } catch (Exception e) {
            log.error("redis Exception: {}", e.getMessage(), e);
        }
    }
}
