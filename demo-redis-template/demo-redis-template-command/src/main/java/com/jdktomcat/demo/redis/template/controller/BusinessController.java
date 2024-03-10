package com.jdktomcat.demo.redis.template.controller;

import com.jdktomcat.demo.redis.template.constant.StringConstant;
import com.jdktomcat.demo.redis.template.component.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：业务入口
 * @date 2024/1/12
 */
@Slf4j
@RestController(value = "/business")
public class BusinessController {

    @Autowired
    private RedisComponent redisComponent;

    @PostMapping("/cache")
    public String addCache(String orderId) {
        String key = StringConstant.CACHE_ORDER + orderId;
        boolean addResult = redisComponent.setIfAbsent(key, StringConstant.ORDER_JSON,3,TimeUnit.SECONDS);
        log.info("添加缓存{}，结果：{}", key, addResult);
        return addResult?"1":"0";
    }

    @DeleteMapping("/del")
    public String del(String orderId) {
        String key = StringConstant.CACHE_ORDER + orderId;
        boolean delResult = redisComponent.del(key);
        log.info("删除缓存{}，结果：{}", key, delResult);
        return "1";
    }

    @GetMapping("/get")
    public String get(String orderId) {
        String key = StringConstant.CACHE_ORDER + orderId;
        try {
            long orderCacheIn = redisComponent.ttl(key);
            if (orderCacheIn == -1 || orderCacheIn == -2) {
                log.info("缓存-查询{} 未查询到缓存订单 已返回NULL_将查询数据库", orderId);
                return null;
            }
            long seconds = redisComponent.getExpire(key);
            if (seconds > 0) {
                log.info("缓存-查询{} 获取到 KEY：{} 已返回缓存订单_将不查询数据库 剩下时长:{}分钟", orderId, key, seconds / 60);
                return redisComponent.get(key).toString();
            } else {
                log.info("缓存-查询{} 已过期 已返回NULL_将查询数据库", orderId);
                return null;
            }
        } catch (Exception e) {
            log.error("缓存-查询{} 异常:{} 已返回NULL_将查询数据库", orderId, e.getMessage(), e);
            return null;
        }
    }
}
