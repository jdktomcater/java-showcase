package com.jdktomcat.showcase.rocketmq.transaction.consumer.listener;

import com.alibaba.fastjson2.JSONObject;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.entry.Order;
import com.jdktomcat.showcase.rocketmq.transaction.consumer.service.IWithdrawOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class MacTransactionListener extends BaseConsumerListener {

    @Autowired
    private IWithdrawOrderService withdrawOrderService;

    @Override
    protected void consume(String tag, String topic, MessageExt messageExt) {
        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8).trim();
        log.info("message body:{}",body);
        Order order = JSONObject.parseObject(body, Order.class);
        withdrawOrderService.saveMac(order);
    }
}
