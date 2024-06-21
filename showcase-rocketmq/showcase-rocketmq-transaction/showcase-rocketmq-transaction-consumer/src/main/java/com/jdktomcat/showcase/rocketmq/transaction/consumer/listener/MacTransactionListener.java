package com.jdktomcat.showcase.rocketmq.transaction.consumer.listener;

import com.alibaba.fastjson2.JSONObject;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.entry.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class MacTransactionListener extends BaseConsumerListener {

    @Override
    protected void consume(String tag, String topic, MessageExt messageExt) {
        String key = messageExt.getKeys();
        try {
            String body = new String(messageExt.getBody(), StandardCharsets.UTF_8).trim();
            Order order = JSONObject.parseObject(body, Order.class);

        } catch (Exception e) {
            log.error("message handle occur exception!{}", key, e);
        }
    }
}
