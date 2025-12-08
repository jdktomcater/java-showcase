package jdktomcat.showcase.mq.rocketmq.pressure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaselineTestConfig {

    @Value("${test.baseline.consumer.groups:10}")
    private int baselineConsumerGroups = 10;

    @Value("${test.baseline.topics:5}")
    private int baselineTopics = 5;

    @Value("${test.baseline.message.rate:1000}")
    private int baselineMessageRate = 1000; // 消息/秒

    @Value("${test.baseline.message.size:1024}")
    private int baselineMessageSize = 1024; // 字节

}
