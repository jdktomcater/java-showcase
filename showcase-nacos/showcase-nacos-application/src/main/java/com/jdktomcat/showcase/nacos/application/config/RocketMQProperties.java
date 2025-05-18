package com.jdktomcat.showcase.nacos.application.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @desc: rocketmq配置信息
 * @author： kidy
 * @createtime： 5/22/20182:55 PM
 * @modify by： ${user}
 * @modify time： 5/22/20182:55 PM
 * @desc of modify：
 * @throws:
 */
@Data
@Component
public class RocketMQProperties {
    @Value("${rocketmq.namesrvAddr}")
    private String namesrvAddr;
    @Value("${rocketmq.consumer.groupName}")
    private String groupName;
    @Value("${rocketmq.consumer.consumeThreadMin}")
    private int consumeThreadMin;
    @Value("${rocketmq.consumer.consumeThreadMax}")
    private int consumeThreadMax;
    @Value("${rocketmq.consumer.batchMaxSize}")
    private int batchMaxSize;
}
