package com.jdktomcat.showcase.mq.rocketmq.transaction.consumer.config;

import com.jdktomcat.showcase.mq.rocketmq.transaction.consumer.listener.BaseConsumerListener;
import com.jdktomcat.showcase.mq.rocketmq.transaction.consumer.listener.MacTransactionListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${rocketmq.consumer.groupName}")
    private String groupName;

    @Value("${rocketmq.consumer.threadMin}")
    private int threadMin;

    @Value("${rocketmq.consumer.threadMax}")
    private int threadMax;

    @Value("${rocketmq.consumer.batchMaxSize}")
    private int batchMaxSize;

    @Value("${showcase.rocketmq.transaction.topic}")
    private String topic;

    @Value("${showcase.rocketmq.transaction.tag}")
    private String tag;


    @Autowired
    private MacTransactionListener macTransactionListener;


    /**
     * 注册新的消費者
     *
     * @return 消费者
     */
    @Bean
    public DefaultMQPushConsumer pushMacTransactionConsumer() {
        return getDefaultMQPushConsumer(macTransactionListener);
    }

    /**
     * 公共属性
     *
     * @return 消费者
     */
    private DefaultMQPushConsumer getDefaultMQPushConsumer(BaseConsumerListener listener){
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName.concat(topic));
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setNamesrvAddr(nameServer);
        consumer.setInstanceName(UUID.randomUUID().toString());
        consumer.setConsumeThreadMin(threadMin);
        consumer.setConsumeThreadMax(threadMax);
        consumer.setConsumeMessageBatchMaxSize(batchMaxSize);
        try {
            consumer.subscribe(topic,tag);
            consumer.registerMessageListener(listener);
            consumer.start();
        } catch (MQClientException e) {
            log.error("MQClientException",e);
        }
        return consumer;
    }
}

