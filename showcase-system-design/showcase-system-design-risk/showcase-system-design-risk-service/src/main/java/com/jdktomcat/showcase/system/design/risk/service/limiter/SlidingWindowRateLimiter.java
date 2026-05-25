package com.jdktomcat.showcase.system.design.risk.service.limiter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

/**
 * Redis-backed sliding window rate limiter using an atomic Lua script.
 *
 * <p>Data structure: Sorted Set  key → { member: requestId, score: epochMillis }
 *
 * <p>Each call atomically:
 * <ol>
 *   <li>Removes entries older than {@code windowSeconds} from the window.</li>
 *   <li>Adds the current request with score = now (epoch ms).</li>
 *   <li>Refreshes the key TTL so expired keys are auto-cleaned by Redis.</li>
 *   <li>Returns the current count in the window.</li>
 * </ol>
 *
 * <p>This is the canonical interview answer for distributed rate limiting at Binance scale:
 * atomic, clock-safe (no TOCTOU race), O(log N) per call.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SlidingWindowRateLimiter {

    private final RedissonClient redissonClient;

    /**
     * Lua script that atomically:
     *  1. Evicts entries outside the window  (ZREMRANGEBYSCORE)
     *  2. Adds the new entry                  (ZADD)
     *  3. Sets TTL to keep Redis memory clean (EXPIRE)
     *  4. Returns the current window count    (ZCARD)
     *
     * KEYS[1] = sorted-set key
     * ARGV[1] = now (epoch ms, string)
     * ARGV[2] = windowMs (window size in ms, string)
     * ARGV[3] = unique member id for the new entry
     */
    private static final String SLIDE_SCRIPT = """
            local key      = KEYS[1]
            local now      = tonumber(ARGV[1])
            local windowMs = tonumber(ARGV[2])
            local member   = ARGV[3]
            local cutoff   = now - windowMs
            redis.call('ZREMRANGEBYSCORE', key, '-inf', cutoff)
            redis.call('ZADD', key, now, member)
            local ttlSecs = math.ceil(windowMs / 1000) + 1
            redis.call('EXPIRE', key, ttlSecs)
            return redis.call('ZCARD', key)
            """;

    /**
     * Count requests that have passed through this key in the last {@code windowSeconds} seconds,
     * and register the current call in the window.
     *
     * @param key           e.g. "risk:velocity:user:12345:WITHDRAWAL:60"
     * @param windowSeconds sliding window size in seconds
     * @return count of requests in the window <em>including</em> this one
     */
    public long countAndRecord(String key, int windowSeconds) {
        long now = System.currentTimeMillis();
        long windowMs = (long) windowSeconds * 1000;
        String member = UUID.randomUUID().toString();

        RScript script = redissonClient.getScript(LongCodec.INSTANCE);
        Object result = script.eval(
                RScript.Mode.READ_WRITE,
                SLIDE_SCRIPT,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(key),
                String.valueOf(now),
                String.valueOf(windowMs),
                member
        );
        return result == null ? 1L : (long) result;
    }

    /**
     * Pure read: count of events in the window without recording a new one.
     * Used by rules that need velocity data without side effects (e.g. structuring detection).
     *
     * Lua: evict stale → ZCARD (no ZADD)
     */
    private static final String COUNT_SCRIPT = """
            local key      = KEYS[1]
            local now      = tonumber(ARGV[1])
            local windowMs = tonumber(ARGV[2])
            local cutoff   = now - windowMs
            redis.call('ZREMRANGEBYSCORE', key, '-inf', cutoff)
            return redis.call('ZCARD', key)
            """;

    public long count(String key, int windowSeconds) {
        long now = System.currentTimeMillis();
        long windowMs = (long) windowSeconds * 1000;

        RScript script = redissonClient.getScript(LongCodec.INSTANCE);
        Object result = script.eval(
                RScript.Mode.READ_WRITE,
                COUNT_SCRIPT,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(key),
                String.valueOf(now),
                String.valueOf(windowMs)
        );
        return result == null ? 0L : (long) result;
    }

    /**
     * Check whether the count in the window exceeds the given limit,
     * recording the current call atomically.
     */
    public boolean isExceeded(String key, int windowSeconds, long limit) {
        return countAndRecord(key, windowSeconds) > limit;
    }

    // --------------- key builders ---------------

    public static String userEventKey(Long userId, String eventType, int windowSeconds) {
        return String.format("risk:velocity:user:%d:%s:%d", userId, eventType, windowSeconds);
    }

    public static String ipEventKey(String ip, String eventType, int windowSeconds) {
        return String.format("risk:velocity:ip:%s:%s:%d", ip, eventType, windowSeconds);
    }

    public static String userAssetKey(Long userId, String assetCode, int windowSeconds) {
        return String.format("risk:velocity:user:%d:asset:%s:%d", userId, assetCode, windowSeconds);
    }
}
