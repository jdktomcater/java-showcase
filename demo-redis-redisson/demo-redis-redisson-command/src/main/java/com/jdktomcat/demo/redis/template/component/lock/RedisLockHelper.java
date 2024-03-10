package com.jdktomcat.demo.redis.template.component.lock;

import com.jdktomcat.demo.redis.template.component.RedisComponent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Redis锁助手，返回的锁使用 try with resource 代码块可以自动关闭
 *
 * @author luxun
 */
@Slf4j
@Component
public class RedisLockHelper {

    private final RedisComponent redisComponent;

    @Autowired
    public RedisLockHelper(RedisComponent redisComponent) {
        this.redisComponent = redisComponent;
    }

    public AutoCloseRedisLock merchantBalanceLock(@NonNull Integer merchantId) throws InterruptedException {
        return lock(RedisLockDef.MERCHANT_BALANCE_LOCK, merchantId.toString());
    }

    public AutoCloseRedisLock lock(RedisLockDef def, String... extData) throws InterruptedException {
        return new AutoCloseRedisLock(def, extData).tryLock();
    }

    /**
     * 使用try with resource方式可以自动关闭锁
     *
     * <pre>
     * try (AutoCloseRedisLock ignore = redisLockHelper.lock(def, extData)) {
     *     // do something
     * } catch (Exception e) {
     *     e.printStackTrace();
     * }
     * </pre>
     */
    public final class AutoCloseRedisLock implements AutoCloseable {

        private final RedisLockDef def;
        private final String suffix;

        private AutoCloseRedisLock(RedisLockDef def, String... extData) {
            this.def = def;
            this.suffix = ":" + String.join("-", extData);
        }

        public AutoCloseRedisLock tryLock() throws InterruptedException {
            String lockKey = def.getKey() + suffix;
            if (redisComponent.tryLock(lockKey, def.getWaitTime(), def.getLeaseTime())) {
                return this;
            }
            throw new InterruptedException("获取锁失败！lock key " + lockKey);
        }

        @Override
        public void close() throws Exception {
            redisComponent.unlock(def.getKey() + suffix);
        }
    }
}
