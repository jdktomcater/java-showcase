package com.jdktomcat.demo.limiter.redis.token.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

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
    private StringRedisTemplate redisStringTemplate;

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
        boolean addResult = Boolean.TRUE.equals(redisStringTemplate.opsForZSet().add(eigenvalue, UUID.randomUUID().toString(), currentTime));
        log.info("添加流控，特征值：{} 分数：{} 结果：{} ",eigenvalue,currentTime,addResult);
        Long removeResult = redisStringTemplate.opsForZSet().removeRangeByScore(eigenvalue, -1,currentTime - window * 1000L);
        log.info("裁剪流控，特征值：{} 分数上限：{} 裁剪结果：{} ", eigenvalue, currentTime - window, removeResult);
        return true;
    }
}
