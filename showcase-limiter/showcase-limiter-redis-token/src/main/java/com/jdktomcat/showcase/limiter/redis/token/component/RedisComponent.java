package com.jdktomcat.showcase.limiter.redis.token.component;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RPermitExpirableSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description desc：redis component
 * @date 2023/11/8
 */
@Slf4j
@Component
public class RedisComponent {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * acquire semaphore permit
     *
     * @param key business key
     * @param lease lease time
     * @return permit token
     */
    public String getSemaphorePermit(String key, Integer lease, Integer token) {
        RPermitExpirableSemaphore permitExpirableSemaphore = redissonClient.getPermitExpirableSemaphore(key);
        if (!permitExpirableSemaphore.isExists()) {
            boolean initResult = permitExpirableSemaphore.trySetPermits(token);
            log.info("semaphore：{},init count：{},init result：{}", key, permitExpirableSemaphore.availablePermits(),initResult);
        }
        log.info("semaphore：{}, available permit：{}", key, permitExpirableSemaphore.availablePermits());
        try{
            return permitExpirableSemaphore.tryAcquire(0,lease, TimeUnit.SECONDS);
        }catch (Exception ex){
            log.error("getSemaphorePermit occur exception!", ex);
            return "";
        }
    }

    /**
     * release semaphore
     *
     * @param key      business key
     * @param permitId permit id
     * @return true:success false：fail
     */
    public boolean releaseSemaphore(String key, String permitId) {
        try{
            RPermitExpirableSemaphore permitExpirableSemaphore = redissonClient.getPermitExpirableSemaphore(key);
            permitExpirableSemaphore.release(permitId);
            log.info("semaphore：{}，available permits：{}", key, permitExpirableSemaphore.availablePermits());
            return true;
        }catch (Exception ex){
            log.error("releaseSemaphore occur exception!", ex);
        }
        return false;
    }
}
