package com.jdktomcat.demo.rocketmq.consumer.config.mq;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @desc: 消費者配置 訂單更新
 * @author： kidy
 * @createtime： 5/22/20183:09 PM
 * @modify by： ${user}
 * @modify time： 5/22/20183:09 PM
 * @desc of modify：
 * @throws:
 */
@Data
@Component
public class ConsumerProperties {
    @Value("${rocketmq.consumer.groupName}")
    private String groupName;
    @Value("${rocketmq.consumer.consumeThreadMin}")
    private int consumeThreadMin;
    @Value("${rocketmq.consumer.consumeThreadMax}")
    private int consumeThreadMax;
    @Value("${rocketmq.consumer.batchMaxSize}")
    private int batchMaxSize;
}
