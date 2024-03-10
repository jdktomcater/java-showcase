package com.jdktomcat.demo.redis.template.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
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
    private RedisTemplate redisTemplate;

    public boolean del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                return redisTemplate.delete(key[0]);
            } else {
                return redisTemplate.delete(CollectionUtils.arrayToList(key)) == key.length;
            }
        }
        return false;
    }

    public boolean set(String key, String value) {
        boolean result = (boolean) redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            connection.set(serializer.serialize(key), serializer.serialize(value));
            return true;
        });
        return result;
    }

    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("redis Exception: {}", e.getMessage(), e);
            return false;
        }
    }


    public long ttl(String key) {
        try {
            return (long) redisTemplate.execute((RedisCallback) connection -> connection.ttl(key.getBytes()));
        } catch (Exception ex) {
            log.error("redis Exception: {}", ex.getMessage(), ex);
            return 0;
        }
    }

    /**
     * 设值,只有在不存在的情况下才设置成功
     *
     * @param key      键
     * @param value    值
     * @param timeout  超时时长
     * @param timeUnit 单位
     * @return true 设置成功 false 设置失败
     */
    public boolean setIfAbsent(String key, String value, long timeout, TimeUnit timeUnit) {
        if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
            Object result = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
                Boolean set = connection.set(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8), Expiration.from(timeout, timeUnit), RedisStringCommands.SetOption.SET_IF_ABSENT);
                log.info("redis设值缓存,key:{} value:{} timeout:{} unit:{} result:{}", key, value, timeout, timeUnit, set);
                return set;
            });
            return Boolean.TRUE.equals(result);
        }
        return false;
    }

    public long getExpire(String key) {
        try {
            return redisTemplate.opsForValue().getOperations().getExpire(key);
        } catch (Exception e) {
            log.error("redis Exception: {}", e.getMessage(), e);
            return 0;
        }
    }

    public Object get(String key) {
        try {
            return key == null ? null : redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            return null;
        }
    }
}
