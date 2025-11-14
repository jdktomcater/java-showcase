package com.jdktomcat.showcase.caffeine;

import com.jdktomcat.showcase.caffeine.task.MatchupRuleTask;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class LocalCaffeineSyncCacheTest extends TestCase {

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testLocalCache() throws InterruptedException {
        int size = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        CountDownLatch latch = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            executorService.submit(new MatchupRuleTask(latch));
        }
        latch.await();
    }
}