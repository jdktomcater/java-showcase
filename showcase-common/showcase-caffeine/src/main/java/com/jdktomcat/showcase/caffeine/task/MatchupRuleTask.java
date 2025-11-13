package com.jdktomcat.showcase.caffeine.task;

import com.jdktomcat.showcase.caffeine.LocalCaffeineSyncCache;
import com.jdktomcat.showcase.caffeine.entry.MatchupRule;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 模拟线上任务
 */
@Slf4j
public class MatchupRuleTask implements Runnable {

    /**
     * 闭锁
     */
    private final CountDownLatch latch;

    public MatchupRuleTask(CountDownLatch latch) {
        this.latch = latch;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            List<MatchupRule> asyncList = LocalCaffeineSyncCache.getAsyncList((i + 1) % 10);
            List<MatchupRule> syncList = LocalCaffeineSyncCache.getSyncList((i + 1) % 10);
            log.info("typeKey:{}, asyncList:{} syncList:{} ", (i + 1) % 10, Arrays.toString(asyncList.toArray()), Arrays.toString(syncList.toArray()));
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
        latch.countDown();
    }
}
