package com.jdktomcat.showcase.redis.redisson.lock.controller;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RequestMapping(value = "/redis")
@Slf4j
public class RedisController {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 心跳检测功能
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/lock", method = RequestMethod.GET)
    public String check(String key) {
        RLock lock = redissonClient.getLock(key);
        lock.lock(500, TimeUnit.MILLISECONDS);
        System.out.println("lock:"+lock.isLocked());
        lock.unlock();
        System.out.println("lock:"+lock.isLocked());
        return "";
    }
}
