package jdktomcat.showcase.rocketmq.pressure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StressTestConfig {

    @Value("${test.stress.consumer.groups:50}")
    private int stressConsumerGroups = 50;

    @Value("${test.stress.topics:20}")
    private int stressTopics = 20;

    @Value("${test.stress.message.rate:5000}")
    private int stressMessageRate = 5000; // 消息/秒

    @Value("${test.stress.message.size:2048}")
    private int stressMessageSize = 2048; // 字节

}
