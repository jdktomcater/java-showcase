package com.jdktomcat.showcase.replace.delay.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * 生产者配置
 */
@Data
@Component
public class ProducerProperties {
    /**
     * 生产群组名称
     */
    @Value("${rocketmq.producer.groupName}")
    private String groupName;

    /**
     * 生产实例名称
     */
    @Value("${rocketmq.producer.instanceName}")
    private String instanceName;

    /**
     * 生产者最大批量数
     */
    @Value("${rocketmq.producer.maxMessageSize}")
    private int maxMessageSize;

    /**
     * 生产者发送消息超时时间
     */
    @Value("${rocketmq.producer.sendMsgTimeout}")
    private int sendMsgTimeout;

    /**
     * 生产者发送消息失败重试次数
     */
    @Value("${rocketmq.producer.retryTimesWhenSendFailed}")
    private int sendRetry;
}
