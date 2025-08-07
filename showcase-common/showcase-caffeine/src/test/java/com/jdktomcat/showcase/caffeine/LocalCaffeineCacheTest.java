package com.jdktomcat.showcase.caffeine;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class LocalCaffeineCacheTest extends TestCase {

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLocalCache() throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            String key = "" + (i % 10);
            String value = LocalCaffeineCache.localSessionCache.get(key);
            log.info("key:{}, value:{}", key, value);
            TimeUnit.SECONDS.sleep(1);
        }
    }
}