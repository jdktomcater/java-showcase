package com.jdktomcat.demo.delay.message.producer.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
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
    private StringRedisTemplate stringRedisTemplate;

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
            log.info("redis设值缓存,key:{} value:{} timeout:{} unit:{}", key, value, timeout, timeUnit);
            return Boolean.TRUE.equals(stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> connection.set(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8), Expiration.from(timeout, timeUnit), RedisStringCommands.SetOption.SET_IF_ABSENT)));
        }
        return false;
    }
}
