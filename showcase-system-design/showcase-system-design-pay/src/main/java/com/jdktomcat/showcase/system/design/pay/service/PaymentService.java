
package com.jdktomcat.showcase.system.design.pay.service;

import org.redisson.api.RedissonClient;
import org.redisson.api.RBucket;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {

    private final RedissonClient redissonClient;

    public PaymentService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void pay(String requestId, Long userId, Long amount) {
        String key = "pay:idem:" + requestId;
        RBucket<String> bucket = redissonClient.getBucket(key);

        if (!bucket.trySet("PROCESSING", 5, TimeUnit.MINUTES)) {
            return;
        }

        // simulate payment
        bucket.set("SUCCESS", 1, TimeUnit.DAYS);
    }
}
