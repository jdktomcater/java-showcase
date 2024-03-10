package com.jdktomcat.demo.rocketmq.consumer.listener;

import com.jdktomcat.demo.rocketmq.consumer.component.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DelayMessageConsumeListener extends BaseConsumerListener {

    /**
     * redis工具类客户端
     */
    @Autowired
    private RedisComponent redisComponent;

    @Override
    protected void consume(String tag, String topic, MessageExt messageExt) {
        String key = messageExt.getKeys();
        try {
            String body = new String(messageExt.getBody(), StandardCharsets.UTF_8).trim();
            if (StringUtils.isNotEmpty(body)) {
                Object value = redisComponent.get(body);
                String valueString = (value == null ? "" : value.toString());
                if (StringUtils.isEmpty(valueString)) {
                    redisComponent.setIfAbsent(body,body,3, TimeUnit.SECONDS);
                }
            }
            log.warn("message :{} handle success！", key);
        } catch (Exception e) {
            log.error("message handle occur exception!{}",key,e);
        }
    }
}
