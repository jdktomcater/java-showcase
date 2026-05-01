package com.jdktomcat.showcase.spring.ai.mcp.gateway.security;

import com.jdktomcat.showcase.spring.ai.mcp.gateway.config.McpGatewayProperties;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class TokenBucketRateLimiter {

    private final Clock clock = Clock.systemUTC();
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean tryAcquire(GatewayPrincipal principal) {
        McpGatewayProperties.RateLimit rateLimit = principal.rateLimit();
        String bucketKey = principal.name() + ":" + rateLimit.getCapacity() + ":" + rateLimit.getRefillPerMinute();
        Bucket bucket = buckets.computeIfAbsent(bucketKey, ignored -> new Bucket(
                Math.max(1, rateLimit.getCapacity()),
                Math.max(1, rateLimit.getRefillPerMinute()),
                clock.millis()
        ));
        return bucket.tryAcquire(clock.millis());
    }

    private static final class Bucket {
        private final int capacity;
        private final double refillPerMillis;
        private double tokens;
        private long lastRefillMillis;

        private Bucket(int capacity, int refillPerMinute, long nowMillis) {
            this.capacity = capacity;
            this.refillPerMillis = refillPerMinute / 60_000.0;
            this.tokens = capacity;
            this.lastRefillMillis = nowMillis;
        }

        private synchronized boolean tryAcquire(long nowMillis) {
            long elapsed = Math.max(0, nowMillis - lastRefillMillis);
            if (elapsed > 0) {
                tokens = Math.min(capacity, tokens + elapsed * refillPerMillis);
                lastRefillMillis = nowMillis;
            }
            if (tokens < 1) {
                return false;
            }
            tokens -= 1;
            return true;
        }
    }
}
