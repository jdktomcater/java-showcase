package com.jdktomcat.showcase.redis.task;

import com.jdktomcat.showcase.redis.component.RedisComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HandleTask implements Runnable {

    /**
     * redis服务
     */
    private final RedisComponent redisComponent;

    public HandleTask(RedisComponent redisComponent) {
        this.redisComponent = redisComponent;
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
        String tableName = "t_third_withdraw_order_example";
        int count = 0;
        for (int i = 0; i < 100000; i++) {
            long index = redisComponent.getIncrementSeq(tableName);
            boolean success = redisComponent.addSet(index);
            if (!success) {
                count++;
                log.error("序列：{} 出现重复id：{}", i, index);
            }
        }
        log.warn("总共有：{} 条重复记录！", count);
    }
}
