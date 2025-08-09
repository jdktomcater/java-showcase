package com.jdktomcat.showcase.caffeine;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class LocalCaffeineCacheTest extends TestCase {

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void setUp() throws Exception {
        super.setUp();
        for (int i = 0; i < 10; i++) {
            LocalCaffeineCache.localSessionCache.put("" + i, "" + System.currentTimeMillis());
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public void testLocalCache() throws InterruptedException {
        for (int i = 0; i < 10000; i++) {
            String key = "" + (i % 10);
            String value = LocalCaffeineCache.localSessionCache.get(key);
            log.info("key:{}, value:{}", key, value);
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }
}