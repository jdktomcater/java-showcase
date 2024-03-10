
package com.jdktomcat.demo.redis.delay.keyspace.notify.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;


/**
 * @author : timmy
 * @Description: 键过期通知监听器
 * @date : 2024/03/02
 */
@Slf4j
@Component
public class KeyExpiredListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 过期的key
        String expiredKey = new String(message.getBody());
        log.info("过期key:{}", expiredKey);
    }
}
