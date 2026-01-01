package com.jdktomcat.showcase.replace.delay.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



/**
 * MQ相关配置
 */
@Data
@Component
public class RocketMQProperties {

    /**
     * MQ服务器路径配置
     */
    @Value("${rocketmq.namesrvAddr}")
    private String namesrvAddr;

    /**
     * 消费者配置
     */
    @Autowired
    private ConsumerProperties consumerProperties;

    /**
     * 生产者配置
     */
    @Autowired
    private ProducerProperties producerProperties;
}
