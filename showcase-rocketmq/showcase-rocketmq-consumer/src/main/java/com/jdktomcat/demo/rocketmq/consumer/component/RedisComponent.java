package com.jdktomcat.demo.rocketmq.consumer.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
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
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate redisStringTemplate;

    public boolean tryLockWithLeaseTime(String lockKey, int leaseTime){
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(0, leaseTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public boolean getLock(String lockKey){
        return Boolean.TRUE.equals(redisStringTemplate.opsForValue().setIfAbsent(lockKey, "1", 2L, TimeUnit.SECONDS));
    }

    public void release(String lockKey){
        redisStringTemplate.delete(lockKey);
    }

    /**
     * 添加集合元素
     *
     * @param keySet 集合键值
     * @param member 元素
     * @return 成功与否
     */
    public Long addSetMember(String keySet, String member){
        return redisStringTemplate.opsForSet().add(keySet,member);
    }

    public Set<String> getSetMember(String keySet){
        return redisStringTemplate.opsForSet().members(keySet);
    }

    public Long add(String keyList,String element){
        return redisStringTemplate.opsForList().rightPush(keyList, element);
    }

    public String pop(String keyList){
        return redisStringTemplate.opsForList().leftPop(keyList);
    }

    public Long listLength(String keyList){
        return redisStringTemplate.opsForList().size(keyList);
    }

    /**
     * 基于固定窗口算法实现限流
     *
     * @param eigenvalue 特征值
     * @param flowLimit  限流阈值
     * @param window     固定窗口(秒)
     * @return true：允许访问 false：不允许访问
     */
    public boolean limitFlowFixedWindow(String eigenvalue, int flowLimit, int window) {
        long currentTime = new Date().getTime();
        if(Boolean.TRUE.equals(redisStringTemplate.hasKey(eigenvalue))) {
            int count = Objects.requireNonNull(redisStringTemplate.opsForZSet().rangeByScore(eigenvalue, currentTime - window * 1000L, currentTime)).size();
            if (count >= flowLimit) {
                log.warn("特征值：{} 已经超过访问频次：{} 时间区间：{}", eigenvalue, flowLimit, window);
                return false;
            }
        }
        redisStringTemplate.opsForZSet().removeRangeByScore(eigenvalue, -1,currentTime - window * 1000L);
        return Boolean.TRUE.equals(redisStringTemplate.opsForZSet().add(eigenvalue, UUID.randomUUID().toString(), currentTime));
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            return key == null ? null : redisStringTemplate.opsForValue().get(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取字符串
     *
     * @param key 键值
     * @return 缓存字符串
     */
    public String getPlainString(String key) {
        return redisStringTemplate.execute((RedisCallback<String>) connection -> {
            byte[] bytes = connection.get(key.getBytes(StandardCharsets.UTF_8));
            if (bytes != null && bytes.length != 0) {
                return new String(bytes, StandardCharsets.UTF_8);
            }
            return "";
        });
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
            Object result = redisStringTemplate.execute((RedisCallback<Boolean>) connection -> {
                Boolean setResult = connection.set(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8), Expiration.from(timeout, timeUnit), RedisStringCommands.SetOption.SET_IF_ABSENT);
                log.info("redis设值缓存,connection:{} key:{} value:{} timeout:{} unit:{} result:{}", connection, key, value, timeout, timeUnit, setResult);
                return setResult;
            });
            log.info("redis设值缓存,key:{} value:{} timeout:{} unit:{} result:{}", key, value, timeout, timeUnit, result);
            return Boolean.TRUE.equals(result);
        }
        return false;
    }

}
