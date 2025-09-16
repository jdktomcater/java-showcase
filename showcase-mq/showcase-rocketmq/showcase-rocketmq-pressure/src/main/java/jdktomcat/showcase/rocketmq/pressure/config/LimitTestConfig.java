package jdktomcat.showcase.rocketmq.pressure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LimitTestConfig {

    @Value("${test.limit.consumer.groups:100}")
    private int limitConsumerGroups = 100;

    @Value("${test.limit.topics:50}")
    private int limitTopics = 50;

    @Value("${test.limit.message.rate:10000}")
    private int limitMessageRate = 10000; // 消息/秒

    @Value("${test.limit.message.size:4096}")
    private int limitMessageSize = 4096; // 字节

}
