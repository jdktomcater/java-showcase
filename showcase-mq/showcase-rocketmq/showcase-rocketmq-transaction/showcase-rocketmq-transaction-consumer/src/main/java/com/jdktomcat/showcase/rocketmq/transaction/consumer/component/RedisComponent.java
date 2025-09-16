package com.jdktomcat.showcase.rocketmq.transaction.consumer.component;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：redis组件
 * @date 2023/11/8
 */
@Slf4j
@Component
public class RedisComponent {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate redisStringTemplate;

    public interface Task {
        void run();
    }

    public  void exeDistSync(String synKey, long wait, long lease, Task task) throws InterruptedException {
        RLock lock = redissonClient.getLock(synKey);
        if(lock.tryLock(wait,lease, TimeUnit.SECONDS)){
            try{
                task.run();
            }finally {
                lock.unlock();
            }
        }
    }

    public Long add(String keyList,String element){
        return redisStringTemplate.opsForList().rightPush(keyList, element);
    }
}
