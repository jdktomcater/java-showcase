package jdktomcat.showcase.rocketmq.pressure.generator;

import jdktomcat.showcase.rocketmq.pressure.tool.ConsumerGroupTestTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class ConsumerGroupGenerator {

    private final RocketMQProperties properties;

    private final List<DefaultMQPushConsumer> consumers = new ArrayList<>();

    public ConsumerGroupGenerator(RocketMQProperties properties) {
        this.properties = properties;
    }

    /**
     * 创建指定数量的消费组
     */
    public List<DefaultMQPushConsumer> createConsumerGroups(int count, String topic, String tag) {
        List<DefaultMQPushConsumer> consumerList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String groupName = String.format("test-consumer-group-%d", i);
            DefaultMQPushConsumer consumer = createConsumer(groupName, topic, tag);
            consumerList.add(consumer);
        }

        return consumerList;
    }

    private DefaultMQPushConsumer createConsumer(String groupName, String topic, String tag) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(properties.getNameServer());
        consumer.setInstanceName(UUID.randomUUID().toString());
        consumer.setConsumeThreadMin(2);
        consumer.setConsumeThreadMax(4);
        consumer.setConsumeMessageBatchMaxSize(10);
        consumer.setMessageModel(MessageModel.CLUSTERING);

        try {
            consumer.subscribe(topic, tag);
            consumer.registerMessageListener(new ConsumerGroupTestTool.TestMessageListener());
            consumer.start();
            log.info("Created consumer group: {}", groupName);
        } catch (MQClientException e) {
            log.error("Failed to create consumer group: {}", groupName, e);
        }

        return consumer;
    }

    /**
     * 清理所有消费组
     */
    public void cleanupConsumers() {
        consumers.forEach(consumer -> {
            try {
                consumer.shutdown();
                log.info("Shutdown consumer: {}", consumer.getConsumerGroup());
            } catch (Exception e) {
                log.error("Failed to shutdown consumer: {}", consumer.getConsumerGroup(), e);
            }
        });
        consumers.clear();
    }
}
