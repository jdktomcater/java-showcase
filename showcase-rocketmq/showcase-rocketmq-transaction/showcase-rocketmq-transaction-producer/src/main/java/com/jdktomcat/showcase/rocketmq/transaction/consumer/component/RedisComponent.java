package com.jdktomcat.showcase.rocketmq.transaction.consumer.component;

import com.jdktomcat.showcase.rocketmq.transaction.consumer.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
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

    /**
     * 日期格式，月日时，例如：20092316
     */
    public static final String DATE_TIME_FORMAT_YYMMDDHHMMSS = "YYMMddHHmmss";
    /**
     * 7位尾数拼接
     */
    public static final long DATE_TIME_BASE = 1000000L;
    /**
     * 7位尾数拼接,超过500万
     */
    public static final long MAX_DATE_TIME_BASE = 5000000L;


    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate redisStringTemplate;

    public boolean tryLockWithLeaseTime(String lockKey, long waitTime, long leaseTime){
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public interface Task {
        void run();
    }

    public  void exeDistSync(String synKey, long wait, long lease, Task task) throws InterruptedException {
        RLock lock = redissonClient.getLock(synKey);
        if(lock.tryLock(wait,lease, TimeUnit.SECONDS)){
            try{
                task.run();
            }finally {
                lock.unlock();
            }
        }
    }

    /**
     * 获取自增序列
     *
     * @param seqKey key名称
     * @return 自增值
     */
    public long getIncrementSeq(String seqKey) {
        // 构造函数传factory会重新创建 redisTemplate 并初始化相关序列化逻辑
        // 直接传入 redisTemplate 避免重新初始化开销
        RedisAtomicLong counter = new RedisAtomicLong(seqKey, Objects.requireNonNull(redisStringTemplate.getConnectionFactory()));
        long inLong = counter.incrementAndGet();
        if (inLong <= 1) {
            counter.expire(10L, TimeUnit.DAYS);
        } else if (inLong == MAX_DATE_TIME_BASE) {
            //超过最大值重置
            counter.expire(1L, TimeUnit.MILLISECONDS);
        } else if (inLong > MAX_DATE_TIME_BASE + 100L) {
            //inLong==MAX_DATE_TIME_BASE处理异常情况下的兼容性处理
            counter.expire(1L, TimeUnit.MILLISECONDS);
        }
        //唯一id：当前时间字符串精确到秒+redis计数
        String nowStr = DateUtil.parseDateToStr(new Date(), DATE_TIME_FORMAT_YYMMDDHHMMSS) + (DATE_TIME_BASE + inLong);
        int iLen = nowStr.length();
        if (iLen > 19) {
            if (iLen == 20) {
                //当长度到达20位时，时间在9月23至9月30，会出现超过long类型范围的情况,需要截取首位，排除掉
                nowStr = nowStr.substring(iLen - 18, iLen);
            } else {
                nowStr = nowStr.substring(iLen - 19, iLen);
            }
        }
        return Long.parseLong(nowStr);
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
