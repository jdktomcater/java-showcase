package com.jdktomcat.demo.delay.message.producer.component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：消息生产者服务组件
 * @date 2023/11/8
 */
@Slf4j
@Component
public class MessageProducerComponent {

    @Resource
    private TransactionMQProducer transactionMQProducer;

    @Value("${demo.rocketmq.topic}")
    private String topic;

    @Value("${demo.rocketmq.tag}")
    private String tag;

    /**
     * 发送消息
     *
     * @param message 消息
     * @param delaySeconds 延迟秒数
     * @return 发送结果
     */
    public SendResult send(String message, int delaySeconds) {
        DelayLevelEnum delayLevelEnum = DelayLevelEnum.findNearbyLevel(delaySeconds);
        Message msg = new Message(topic, tag, message, message.getBytes(StandardCharsets.UTF_8));
        msg.setDelayTimeLevel(delayLevelEnum.getLevel());
        transactionMQProducer.setRetryTimesWhenSendAsyncFailed(3);
        try {
            log.info("异步发送延迟消息：{}", msg);
            return transactionMQProducer.send(msg);
        }catch (Exception ex){
            log.error("同步发送消息异常：",ex);
        }
        return null;
    }
}

