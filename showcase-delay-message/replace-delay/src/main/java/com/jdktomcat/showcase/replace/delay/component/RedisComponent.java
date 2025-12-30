package com.jdktomcat.showcase.replace.delay.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

import static com.jdktomcat.showcase.replace.delay.constant.ReplaceDelayConstant.DEFAULT_LIST;

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
     * 递增
     *
     * @param key 键
     * @return 现在值
     */
    public long incr(String key) {
        if (StringUtils.isEmpty(key)) {
            key = DEFAULT_LIST;
        }
        Long value = stringRedisTemplate.opsForValue().increment(key, 1);
        return value == null ? 0L : value;
    }


    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return 操作结果 true 设置成功 false 设置失败
     */
    public boolean expire(String key, long time) {
        try {
            return stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis Exception: {}", e.getMessage(), e);
            return false;
        }
    }
}
