package com.jdktomcat.showcase.redis.redisson.lock.controller;

import com.jdktomcat.showcase.redis.redisson.lock.component.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/redis")
@Slf4j
public class RedisController {

    @Autowired
    private RedisComponent redisComponent;

    @RequestMapping(value = "/lock", method = RequestMethod.GET)
    public String lock(String key) {
        try {
            RLock lock = redisComponent.getLock(key);
            log.info("lock:{}", lock.isLocked());
            if (lock.isLocked()) {
                lock.unlock();
                log.info("lock:{}", lock.isLocked());
                return "ok";
            } else {
                return "fail";
            }
        } catch (Exception e) {
            log.error("lock error", e);
            return "error";
        }
    }
}
