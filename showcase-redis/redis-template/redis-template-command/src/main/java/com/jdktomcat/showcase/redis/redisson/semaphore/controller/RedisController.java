package com.jdktomcat.showcase.redis.redisson.semaphore.controller;

import com.jdktomcat.showcase.redis.redisson.semaphore.component.RedisComponent;
import com.jdktomcat.showcase.redis.redisson.semaphore.constant.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisComponent redisComponent;


    @GetMapping(value = "/getOps")
    public String getOps(String orderId) {
        Object value = redisComponent.get(StringConstant.CACHE_ORDER + orderId);
        return value != null ? value.toString() : null;
    }

    @GetMapping(value ="/getExe")
    public String getExe(String orderId) {
        return redisComponent.getExe(StringConstant.CACHE_ORDER + orderId);
    }

}
