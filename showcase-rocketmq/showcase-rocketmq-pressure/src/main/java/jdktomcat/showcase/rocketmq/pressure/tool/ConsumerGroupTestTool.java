package jdktomcat.showcase.rocketmq.pressure.tool;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class ConsumerGroupTestTool {

    @Value("${rocketmq.namesrvAddr:localhost:9876}")
    private String namesrvAddr;

    @Value("${test.producer.group:test-producer-group}")
    private String producerGroup;

    @Value("${test.topic:test-topic}")
    private String testTopic;

    @Value("${test.tag:test-tag}")
    private String testTag;

    private final AtomicLong messageCounter = new AtomicLong(0);
    private final ConcurrentHashMap<String, DefaultMQPushConsumer> consumers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService monitorExecutor = Executors.newScheduledThreadPool(1);

    /**
     * 执行消费组影响测试
     */
    public void runConsumerGroupTest(int consumerGroupCount, int messageRate, int durationSeconds) {
        log.info("开始消费组影响测试 - 消费组数量: {}, 消息速率: {}, 持续时间: {}秒", consumerGroupCount, messageRate, durationSeconds);

        try {
            // 1. 创建消费组
            createConsumerGroups(consumerGroupCount);

            // 2. 启动性能监控
            startPerformanceMonitoring();

            // 3. 发送测试消息
            sendTestMessages(messageRate, durationSeconds);

            // 4. 等待测试完成
            Thread.sleep(durationSeconds * 1000L);

            // 5. 清理资源
            cleanup();

        } catch (Exception e) {
            log.error("测试执行失败", e);
        }
    }

    /**
     * 创建指定数量的消费组
     */
    private void createConsumerGroups(int count) {
        log.info("创建 {} 个消费组", count);

        for (int i = 0; i < count; i++) {
            String groupName = String.format("test-consumer-group-%d", i);
            DefaultMQPushConsumer consumer = createConsumer(groupName);
            consumers.put(groupName, consumer);
        }

        log.info("成功创建 {} 个消费组", consumers.size());
    }

    /**
     * 创建单个消费组
     */
    private DefaultMQPushConsumer createConsumer(String groupName) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setInstanceName(UUID.randomUUID().toString());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setConsumeThreadMin(2);
        consumer.setConsumeThreadMax(4);
        consumer.setConsumeMessageBatchMaxSize(10);
        consumer.setMessageModel(MessageModel.CLUSTERING);

        try {
            consumer.subscribe(testTopic, testTag);
            consumer.registerMessageListener(new TestMessageListener());
            consumer.start();
            log.info("成功创建消费组: {}", groupName);
        } catch (MQClientException e) {
            log.error("创建消费组失败: {}", groupName, e);
        }

        return consumer;
    }

    /**
     * 启动性能监控
     */
    private void startPerformanceMonitoring() {
        monitorExecutor.scheduleAtFixedRate(() -> {
            try {
                collectAndLogMetrics();
            } catch (Exception e) {
                log.error("性能监控异常", e);
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * 收集并记录性能指标
     */
    private void collectAndLogMetrics() {
        // 获取系统资源使用情况
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUsagePercent = (double) usedMemory / totalMemory * 100;

        // 获取消费组数量
        int consumerGroupCount = consumers.size();

        // 记录指标
        log.info("性能指标 - 消费组数量: {}, 内存使用: {:.2f}%, 活跃消费组: {}",
                consumerGroupCount, memoryUsagePercent, getActiveConsumerCount());

        // 检查性能阈值
        checkPerformanceThresholds(consumerGroupCount, memoryUsagePercent);
    }

    /**
     * 检查性能阈值
     */
    private void checkPerformanceThresholds(int consumerGroupCount, double memoryUsagePercent) {
        if (consumerGroupCount > 50) {
            log.warn("消费组数量过多: {}", consumerGroupCount);
        }

        if (memoryUsagePercent > 80) {
            log.warn("内存使用率过高: {:.2f}%", memoryUsagePercent);
        }

        if (memoryUsagePercent > 90) {
            log.error("内存使用率严重过高: {:.2f}%", memoryUsagePercent);
        }
    }

    /**
     * 获取活跃消费组数量
     */
    private int getActiveConsumerCount() {
        return (int) consumers.values().stream()
                .filter(consumer -> consumer.getDefaultMQPushConsumerImpl().getServiceState().name().equals("RUNNING"))
                .count();
    }

    /**
     * 发送测试消息
     */
    private void sendTestMessages(int messageRate, int durationSeconds) {
        log.info("开始发送测试消息 - 速率: {} 消息/秒, 持续时间: {} 秒", messageRate, durationSeconds);

        DefaultMQProducer producer = null;
        try {
            producer = new DefaultMQProducer(producerGroup);
            producer.setNamesrvAddr(namesrvAddr);
            producer.setInstanceName("test-producer-" + UUID.randomUUID().toString());
            producer.start();

            int totalMessages = messageRate * durationSeconds;
            int messagesPerBatch = Math.min(messageRate, 100); // 每批最多100条消息
            int batchCount = totalMessages / messagesPerBatch;

            for (int batch = 0; batch < batchCount; batch++) {
                sendMessageBatch(producer, messagesPerBatch);

                // 控制发送速率
                if (batch < batchCount - 1) {
                    Thread.sleep(1000); // 每秒发送一批
                }
            }

        } catch (Exception e) {
            log.error("发送测试消息失败", e);
        } finally {
            if (producer != null) {
                producer.shutdown();
            }
        }
    }

    /**
     * 发送一批消息
     */
    private void sendMessageBatch(DefaultMQProducer producer, int batchSize) {
        for (int i = 0; i < batchSize; i++) {
            try {
                String messageBody = generateTestMessage(1024);
                String keys = String.format("test-msg-%d", messageCounter.incrementAndGet());
                Message msg = new Message(testTopic, testTag, keys, messageBody.getBytes());
                SendResult result = producer.send(msg);
                if (result.getSendStatus() == SendStatus.SEND_OK) {
                    log.debug("发送消息成功: {}", keys);
                } else {
                    log.warn("发送消息失败: {}", keys);
                }
            } catch (Exception e) {
                log.error("发送消息异常", e);
            }
        }
    }

    /**
     * 生成测试消息
     */
    private String generateTestMessage(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append((char) ('a' + (i % 26)));
        }
        return sb.toString();
    }

    /**
     * 清理资源
     */
    private void cleanup() {
        log.info("开始清理测试资源");

        // 关闭所有消费组
        consumers.values().forEach(consumer -> {
            try {
                consumer.shutdown();
                log.info("关闭消费组: {}", consumer.getConsumerGroup());
            } catch (Exception e) {
                log.error("关闭消费组失败: {}", consumer.getConsumerGroup(), e);
            }
        });
        consumers.clear();

        // 关闭监控线程池
        monitorExecutor.shutdown();
        try {
            if (!monitorExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                monitorExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            monitorExecutor.shutdownNow();
        }

        log.info("测试资源清理完成");
    }

    /**
     * 测试消息监听器
     */
    public static class TestMessageListener implements MessageListenerConcurrently {

        private final AtomicLong consumedCount = new AtomicLong(0);

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt msg : msgs) {
                try {
                    // 模拟消息处理
                    processMessage(msg);
                    consumedCount.incrementAndGet();

                    if (consumedCount.get() % 100 == 0) {
                        log.info("已消费消息数量: {}", consumedCount.get());
                    }
                } catch (Exception e) {
                    log.error("处理消息失败: {}", msg.getKeys(), e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        private void processMessage(MessageExt msg) {
            // 模拟消息处理逻辑
            String messageBody = new String(msg.getBody());
            log.debug("处理消息: topic={}, tag={}, keys={}, bodyLength={}",
                    msg.getTopic(), msg.getTags(), msg.getKeys(), messageBody.length());

            // 模拟处理时间
            try {
                Thread.sleep(10); // 10ms处理时间
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 获取测试统计信息
     */
    public TestStatistics getTestStatistics() {
        return TestStatistics.builder()
                .consumerGroupCount(consumers.size())
                .activeConsumerCount(getActiveConsumerCount())
                .totalMessagesSent(messageCounter.get())
                .build();
    }

    /**
     * 测试统计信息
     */
    @lombok.Builder
    @lombok.Data
    public static class TestStatistics {
        private int consumerGroupCount;
        private int activeConsumerCount;
        private long totalMessagesSent;
        private double memoryUsagePercent;
        private double cpuUsagePercent;
    }
}
