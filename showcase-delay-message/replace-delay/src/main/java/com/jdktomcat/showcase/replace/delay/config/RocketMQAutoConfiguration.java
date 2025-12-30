package com.jdktomcat.showcase.replace.delay.config;

import com.jdktomcat.showcase.replace.delay.listener.BaseConsumerListener;
import com.jdktomcat.showcase.replace.delay.listener.DelayMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * MQ自动配置类
 */
@Slf4j
@Configuration
public class RocketMQAutoConfiguration {

    /**
     * MQ相关的配置
     */
    @Autowired
    private RocketMQProperties properties;

    /**
     * 延迟消息消费组件
     */
    @Autowired
    private DelayMessageListener delayMessageListener;

    /**
     * 注册新的延迟消息消費者
     *
     * @return 消费者
     */
    @Bean
    public DefaultMQPushConsumer delayMessageConsumer() {
        return getClusterMQPushConsumer(delayMessageListener);
    }

    /**
     * 初始化事物消息生產者
     *
     * @return 消息生产者
     * @throws MQClientException 异常
     */
    @Bean
    public TransactionMQProducer transactionMQProducer() throws MQClientException {
        // 设置生产者组
        TransactionMQProducer producer = new TransactionMQProducer(properties.getProducerProperties().getGroupName());
        // 设置服务器地址
        producer.setNamesrvAddr(properties.getNamesrvAddr());
        // 设置生产者实例名称
        producer.setInstanceName(properties.getProducerProperties().getInstanceName());
        // 设置最大消息体大小
        producer.setMaxMessageSize(properties.getProducerProperties().getMaxMessageSize());
        // 设置发送消息超时时间
        producer.setSendMsgTimeout(properties.getProducerProperties().getSendMsgTimeout());
        // 设置发送失败重拾次数
        producer.setRetryTimesWhenSendFailed(properties.getProducerProperties().getSendRetry());
        // 设置是否为VIP通道
        producer.setVipChannelEnabled(false);
        // 生产者启动
        producer.start();
        return producer;
    }


    /**
     * 常规消费者注册
     *
     * @param listener 消费者
     * @return 消费者对象
     */
    private DefaultMQPushConsumer getBaseMQPushConsumer(BaseConsumerListener listener, MessageModel messageModel){
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(properties.getConsumerProperties().getGroupName().concat(listener.topic()));
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
        consumer.setNamesrvAddr(properties.getNamesrvAddr());
        consumer.setInstanceName(UUID.randomUUID().toString());
        consumer.setConsumeThreadMin(properties.getConsumerProperties().getConsumeThreadMin());
        consumer.setConsumeThreadMax(properties.getConsumerProperties().getConsumeThreadMax());
        consumer.setConsumeMessageBatchMaxSize(properties.getConsumerProperties().getBatchMaxSize());
        consumer.setMessageModel(messageModel);
        try {
            consumer.subscribe(listener.topic(), listener.tags());
            consumer.registerMessageListener(listener);
            consumer.start();
        } catch (MQClientException e) {
            log.error("注册消费者过程中产生异常！",e);
        }
        return consumer;
    }

    /**
     * 常规集群消费者注册
     *
     * @param listener 消费者
     * @return 消费者对象
     */
    private DefaultMQPushConsumer getClusterMQPushConsumer(BaseConsumerListener listener){
        return getBaseMQPushConsumer(listener, MessageModel.CLUSTERING);
    }
}
