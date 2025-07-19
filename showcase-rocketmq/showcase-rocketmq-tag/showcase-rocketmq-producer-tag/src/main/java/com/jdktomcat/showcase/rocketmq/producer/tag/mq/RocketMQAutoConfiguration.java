package com.jdktomcat.showcase.rocketmq.producer.tag.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;



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

}

