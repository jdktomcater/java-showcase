package com.jdktomcat.showcase.caffeine;

import junit.framework.TestCase;

import java.util.concurrent.TimeUnit;

public class LocalCaffeineCacheTest extends TestCase {

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLocalCache() throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            String value = LocalCaffeineCache.localSessionCache.get("key:" + i % 10);
            TimeUnit.SECONDS.sleep(1);
        }
    }
}