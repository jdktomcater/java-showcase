package com.jdktomcat.showcase.redis.redisson.lock.controller;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/redis")
@Slf4j
public class RedisController {

    @Resource
    private RedissonClient redissonClient;

    @RequestMapping(value = "/lock", method = RequestMethod.GET)
    public String lock(String key) {
        try{
            RLock lock = redissonClient.getLock(key);
            lock.lock(500, TimeUnit.MILLISECONDS);
            log.info("lock:{}", lock.isLocked());
            lock.unlock();
            log.info("lock:{}", lock.isLocked());
        }catch (Exception e){
            log.error("lock error", e);
            return "error";
        }
        return "ok";
    }
}
