package com.jdktomcat.demo.rocketmq.consumer.config.mq;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
    @Resource
    private ConsumerProperties consumerProperties;
}
