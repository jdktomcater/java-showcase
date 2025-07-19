package com.jdktomcat.demo.rocketmq.consumer.tag.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class WithdrawMessageConsumerListener extends BaseConsumerListener {

    @Override
    protected void consume(String tag, String topic, MessageExt messageExt) {
        String key = messageExt.getKeys();
        try {
            String body = new String(messageExt.getBody(), StandardCharsets.UTF_8).trim();
            log.warn("message :{} body:{} handle success！", key, body);
        } catch (Exception e) {
            log.error("message handle occur exception!{}", key, e);
        }
    }
}
