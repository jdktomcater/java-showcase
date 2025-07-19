package com.jdktomcat.showcase.rocketmq.producer.tag.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;


/**
 * Rocketmq消息中間件生產者與消費者配置
 */
@Slf4j
@Configuration
public class RocketMQAutoConfiguration {

    @Value("${rocketmq.name-server}")
    private String nameServer;

    @Value("${rocketmq.producer.group}")
    private String groupName;

    @Value("${rocketmq.producer.max-message-size}")
    private int maxMessageSize;

    @Value("${rocketmq.producer.send-message-timeout}")
    private int sendMsgTimeout;


    /**
     * 消息发送者定义
     *
     * @return 消息发送者
     */
    @Bean
    public TransactionMQProducer transactionMQProducer() throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer(groupName);
        producer.setNamesrvAddr(nameServer);
        producer.setInstanceName("tag-producer-" + UUID.randomUUID());
        producer.setMaxMessageSize(maxMessageSize);
        producer.setSendMsgTimeout(sendMsgTimeout);
        producer.setVipChannelEnabled(false);
        producer.start();
        return  producer;
    }

}

