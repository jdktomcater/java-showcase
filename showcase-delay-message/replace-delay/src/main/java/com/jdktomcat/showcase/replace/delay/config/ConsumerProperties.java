package com.jdktomcat.showcase.replace.delay.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 消费者配置项
 */
@Data
@Component
public class ConsumerProperties {

    /**
     * 消费组名称
     */
    @Value("${rocketmq.consumer.groupName}")
    private String groupName;

    /**
     * 消费实例名称
     */
    @Value("${rocketmq.consumer.instanceName}")
    private String instanceName;

    /**
     * 消费最小线程数
     */
    @Value("${rocketmq.consumer.consumeThreadMin}")
    private int consumeThreadMin;

    /**
     * 消费最大线程数
     */
    @Value("${rocketmq.consumer.consumeThreadMax}")
    private int consumeThreadMax;

    /**
     * 批次处理条数
     */
    @Value("${rocketmq.consumer.batchMaxSize}")
    private int batchMaxSize;
}
