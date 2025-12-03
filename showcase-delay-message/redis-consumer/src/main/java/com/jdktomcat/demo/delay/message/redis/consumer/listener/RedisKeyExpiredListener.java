package com.jdktomcat.demo.delay.message.redis.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * @author : timmy
 * @Description: redis键过期通知监听器
 * @date : 2023年11月24日 20:15
 */
@Slf4j
@Component
public class RedisKeyExpiredListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 过期的key
        String expiredKey = new String(message.getBody());
        log.info("redis consume key:{}", expiredKey);
    }
}
