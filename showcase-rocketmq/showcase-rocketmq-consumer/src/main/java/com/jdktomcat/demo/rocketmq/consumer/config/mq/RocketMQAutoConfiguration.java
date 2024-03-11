package com.jdktomcat.demo.rocketmq.consumer.config.mq;

import com.jdktomcat.demo.rocketmq.consumer.constant.RocketMQConstant;
import com.jdktomcat.demo.rocketmq.consumer.listener.BaseConsumerListener;
import com.jdktomcat.demo.rocketmq.consumer.listener.DelayMessageConsumeListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static com.jdktomcat.demo.rocketmq.consumer.constant.RocketMQConstant.DELAY_MESSAGE_TOPIC;


/**
 * Rocketmq消息中間件生產者與消費者配置
 */
@Slf4j
@Configuration
public class RocketMQAutoConfiguration {

    @Autowired
    private RocketMQProperties properties;

    /**
     * 延迟消息消费者
     */
    @Autowired
    private DelayMessageConsumeListener delayMessageConsumeListener;

    /**
     * 注册新的消費者
     *
     * @return
     * @throws MQClientException
     */
    @Bean
    public DefaultMQPushConsumer pushDelayMessageConsumer() {
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
        consumer.setInstanceName(UUID.randomUUID().toString());
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

