package com.jdktomcat.showcase.rocketmq.transaction.producer.component;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：消息生产者服务组件
 * @date 2024/06/18
 */
@Component
public class MqProducerComponent {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @Value("${showcase.rocketmq.transaction.topic}")
    private String transactionTopic;



    /**
     * 发送事务消息
     *
     * @param message 消息
     * @return 发送结果
     */
    public TransactionSendResult sendTransactionMessage(String message) {
        return rocketMQTemplate.sendMessageInTransaction(transactionTopic, MessageBuilder.withPayload(message).build(),null);
    }
}
