package com.jdktomcat.showcase.snowflake.redis.component;

import com.jdktomcat.showcase.snowflake.redis.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取自增序列
     *
     * @param seqKey key名称
     * @return 自增值
     */
    public long getIncrementSeq(String seqKey) {
        // 直接传入 redisTemplate 避免重新初始化开销
        RedisAtomicLong counter = new RedisAtomicLong(seqKey, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
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

    private static final String SNOWFLAKE_SET_KEY = "SNOWFLAKE_SET_KEY";

    /**
     * 添加集合
     *
     * @param id 元素
     * @return 是否成功
     */
    public boolean addSet(Long id) {
        Long result = redisTemplate.opsForSet().add(SNOWFLAKE_SET_KEY, id);
        log.info("add id:{} result:{}", id, result);
        return result != null && result > 0;
    }
}
