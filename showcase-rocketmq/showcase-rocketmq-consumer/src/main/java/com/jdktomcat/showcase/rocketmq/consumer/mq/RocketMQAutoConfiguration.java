package com.jdktomcat.showcase.rocketmq.consumer.mq;

import com.jdktomcat.showcase.rocketmq.consumer.constant.RocketMQConstant;
import com.jdktomcat.showcase.rocketmq.consumer.listener.BaseConsumerListener;
import com.jdktomcat.showcase.rocketmq.consumer.listener.DelayMessageConsumeListener;
import com.jdktomcat.showcase.rocketmq.consumer.listener.DepositMessageConsumerListener;
import com.jdktomcat.showcase.rocketmq.consumer.listener.WithdrawMessageConsumerListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Map;

import static com.jdktomcat.showcase.rocketmq.consumer.constant.RocketMQConstant.*;


/**
 * Rocketmq消息中間件生產者與消費者配置
 */
@Slf4j
@Configuration
public class RocketMQAutoConfiguration {

    @Value("${node:0}")
    private int node;

    @Autowired
    private RocketMQProperties properties;


    @Autowired
    private WithdrawMessageConsumerListener withdrawMessageConsumerListener;

    @Autowired
    private DepositMessageConsumerListener depositMessageConsumerListener;

    @Autowired
    private DelayMessageConsumeListener delayMessageConsumeListener;

    /**
     * 注册新的消費者
     *
     * @return
     * @throws MQClientException
     */
    @ConditionalOnProperty(name = "name",prefix = "spring.application", havingValue = "consumer-user")
    @Bean
    public DefaultMQPushConsumer pushWithdrawMessageConsumer() {
        return getDefaultMQPushConsumer(WITHDRAW_MESSAGE_TOPIC, withdrawMessageConsumerListener);
    }

    /**
     * 注册新的消費者
     *
     * @return
     * @throws MQClientException
     */
    @Bean
    public DefaultMQPushConsumer pushDepositMessageConsumer() {
        return getDefaultMQPushConsumer(DEPOSIT_MESSAGE_TOPIC, depositMessageConsumerListener);
    }

    /**
     * 注册新的消費者
     *
     * @return
     * @throws MQClientException
     */
    @Bean

    public DefaultMQPushConsumer delayMessageConsumer() {
        return getDefaultMQPushConsumer(DELAY_MESSAGE_TOPIC, delayMessageConsumeListener);
    }

    /**
     * 公共属性
     * @param suffix topic为后缀
     * @return
     */
    private DefaultMQPushConsumer getDefaultMQPushConsumer(String suffix, BaseConsumerListener listener){
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(properties.getConsumerProperties().getGroupName().concat(suffix));
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setNamesrvAddr(properties.getNamesrvAddr());
        consumer.setInstanceName("node-"+node);
        consumer.setConsumeThreadMin(properties.getConsumerProperties().getConsumeThreadMin());
        consumer.setConsumeThreadMax(properties.getConsumerProperties().getConsumeThreadMax());
        consumer.setConsumeMessageBatchMaxSize(properties.getConsumerProperties().getBatchMaxSize());
        String tagsTtr = getTags(suffix);
        try {
            consumer.subscribe(suffix,tagsTtr);
            consumer.registerMessageListener(listener);
            consumer.start();
        } catch (MQClientException e) {
            log.error("MQClientException",e);
        }
        return consumer;
    }

    /**
     * 多个tag用||拼接
     * @param topic
     * @return tagA||tagB...
     */
    private String getTags(String topic) {
        Map<String,String[]> stringMap = RocketMQConstant.fetchTopicArray();
        String[] strings = stringMap.get(topic);
        if (strings!=null && strings.length!=0){
            String string = Arrays.asList(strings).toString();
            string = string.substring(1,string.length()-1);
            if (string.contains(",")){
                return string.replaceAll(",","||");
            }
            return string;
        }
        return "";
    }
}

