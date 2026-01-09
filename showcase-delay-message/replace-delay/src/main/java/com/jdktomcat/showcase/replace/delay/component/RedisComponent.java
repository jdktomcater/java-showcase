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
 * @date 2025/12/30
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
     * 哈希设置
     *
     * @param key   键
     * @param field 属性
     * @param value 值
     */
    public boolean hset(String key, String field, String value) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        return stringRedisTemplate.opsForHash().putIfAbsent(key, field, value);
    }

    /**
     * 检查是否存在
     *
     * @param key   键
     * @param field 属性
     * @return true：存在 false：不存在
     */
    public boolean hexists(String key, String field) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        return stringRedisTemplate.opsForHash().hasKey(key, field);
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

    /**
     * 根据key精确匹配删除
     *
     * @param key 删除目标键
     */
    public boolean del(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 检查key是否存在
     *
     * @param key 键
     * @return true:存在 false：不存在
     */
    public boolean exist(String key){
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 键离过期时间毫秒
     *
     * @param key 键
     * @return 毫秒
     */
    public long pttl(String key) {
        try {
            return stringRedisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("redis pttl command Exception: {}", ex.getMessage(), ex);
            return 0L;
        }
    }

}
