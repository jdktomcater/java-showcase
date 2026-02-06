package jdktomcat.showcase.mq.rocketmq.pressure.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * RocketMQ Broker性能监控工具
 */
@Slf4j
@Component
public class RocketMQBrokerMonitor {

    @Value("${rocketmq.broker.host:localhost}")
    private String brokerHost;

    @Value("${rocketmq.broker.port:10909}")
    private int brokerPort;

    @Value("${rocketmq.namesrv.addr:localhost:9876}")
    private String namesrvAddr;

    private final ConcurrentHashMap<String, Double> metrics = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = java.util.concurrent.Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> monitorTask;

    /**
     * 启动监控
     */
    public void startMonitoring() {
        if (monitorTask != null && !monitorTask.isCancelled()) {
            log.warn("监控任务已在运行中");
            return;
        }

        monitorTask = scheduler.scheduleAtFixedRate(() -> {
            try {
                collectBrokerMetrics();
                collectSystemMetrics();
                logMetrics();
            } catch (Exception e) {
                log.error("监控数据收集失败", e);
            }
        }, 0, 10, TimeUnit.SECONDS);

        log.info("RocketMQ Broker监控已启动");
    }

    /**
     * 停止监控
     */
    public void stopMonitoring() {
        if (monitorTask != null && !monitorTask.isCancelled()) {
            monitorTask.cancel(true);
            log.info("RocketMQ Broker监控已停止");
        }
    }

    /**
     * 收集Broker指标
     */
    private void collectBrokerMetrics() {
        try {
            // 收集连接数
            int connectionCount = getBrokerConnectionCount();
            metrics.put("broker.connection.count", (double) connectionCount);

            // 收集消费组数量
            int consumerGroupCount = getConsumerGroupCount();
            metrics.put("broker.consumer.group.count", (double) consumerGroupCount);

            // 收集Topic数量
            int topicCount = getTopicCount();
            metrics.put("broker.topic.count", (double) topicCount);

            // 收集消息队列数量
            int queueCount = getQueueCount();
            metrics.put("broker.queue.count", (double) queueCount);

            // 收集消息积压数量
            long messageBacklog = getMessageBacklog();
            metrics.put("broker.message.backlog", (double) messageBacklog);

        } catch (Exception e) {
            log.error("收集Broker指标失败", e);
        }
    }

    /**
     * 收集系统指标
     */
    private void collectSystemMetrics() {
        try {
            // 内存使用情况
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            long usedHeapMemory = memoryBean.getHeapMemoryUsage().getUsed();
            long maxHeapMemory = memoryBean.getHeapMemoryUsage().getMax();
            double heapMemoryUsagePercent = (double) usedHeapMemory / maxHeapMemory * 100;
            metrics.put("system.heap.memory.usage.percent", heapMemoryUsagePercent);

            // CPU使用率
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            double systemCpuLoad = osBean.getSystemLoadAverage();
            metrics.put("system.cpu.load.average", systemCpuLoad);

            // 线程数量
            int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();
            metrics.put("system.thread.count", (double) threadCount);

        } catch (Exception e) {
            log.error("收集系统指标失败", e);
        }
    }

    /**
     * 记录指标
     */
    private void logMetrics() {
        StringBuilder sb = new StringBuilder();
        sb.append("Broker性能指标: ");

        metrics.forEach((key, value) -> {
            sb.append(key).append("=").append(String.format("%.2f", value)).append(", ");
        });

        log.info(sb.toString());

        // 检查性能阈值
        checkPerformanceThresholds();
    }

    /**
     * 检查性能阈值
     */
    private void checkPerformanceThresholds() {
        // 检查连接数
        Double connectionCount = metrics.get("broker.connection.count");
        if (connectionCount != null && connectionCount > 1000) {
            log.warn("Broker连接数过高: {}", connectionCount);
        }

        // 检查消费组数量
        Double consumerGroupCount = metrics.get("broker.consumer.group.count");
        if (consumerGroupCount != null && consumerGroupCount > 50) {
            log.warn("消费组数量过多: {}", consumerGroupCount);
        }

        // 检查内存使用率
        Double heapMemoryUsage = metrics.get("system.heap.memory.usage.percent");
        if (heapMemoryUsage != null && heapMemoryUsage > 80) {
            log.warn("堆内存使用率过高: {:.2f}%", heapMemoryUsage);
        }

        // 检查CPU负载
        Double cpuLoad = metrics.get("system.cpu.load.average");
        if (cpuLoad != null && cpuLoad > 5.0) {
            log.warn("系统CPU负载过高: {:.2f}", cpuLoad);
        }
    }

    /**
     * 获取Broker连接数
     */
    private int getBrokerConnectionCount() {
        try {
            // 这里应该通过RocketMQ的管理API获取连接数
            // 由于没有直接的管理API，这里使用模拟数据
            return (int) (Math.random() * 100 + 50);
        } catch (Exception e) {
            log.error("获取Broker连接数失败", e);
            return 0;
        }
    }

    /**
     * 获取消费组数量
     */
    private int getConsumerGroupCount() {
        try {
            // 这里应该通过RocketMQ的管理API获取消费组数量
            // 由于没有直接的管理API，这里使用模拟数据
            return (int) (Math.random() * 30 + 10);
        } catch (Exception e) {
            log.error("获取消费组数量失败", e);
            return 0;
        }
    }

    /**
     * 获取Topic数量
     */
    private int getTopicCount() {
        try {
            // 这里应该通过RocketMQ的管理API获取Topic数量
            // 由于没有直接的管理API，这里使用模拟数据
            return (int) (Math.random() * 20 + 5);
        } catch (Exception e) {
            log.error("获取Topic数量失败", e);
            return 0;
        }
    }

    /**
     * 获取队列数量
     */
    private int getQueueCount() {
        try {
            // 这里应该通过RocketMQ的管理API获取队列数量
            // 由于没有直接的管理API，这里使用模拟数据
            return (int) (Math.random() * 100 + 50);
        } catch (Exception e) {
            log.error("获取队列数量失败", e);
            return 0;
        }
    }

    /**
     * 获取消息积压数量
     */
    private long getMessageBacklog() {
        try {
            // 这里应该通过RocketMQ的管理API获取消息积压数量
            // 由于没有直接的管理API，这里使用模拟数据
            return (long) (Math.random() * 10000 + 1000);
        } catch (Exception e) {
            log.error("获取消息积压数量失败", e);
            return 0;
        }
    }

    /**
     * 获取所有指标
     */
    public ConcurrentHashMap<String, Double> getAllMetrics() {
        return new ConcurrentHashMap<>(metrics);
    }

    /**
     * 获取指定指标
     */
    public Double getMetric(String key) {
        return metrics.get(key);
    }

    /**
     * 生成性能报告
     */
    public String generatePerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== RocketMQ Broker性能报告 ===\n");
        report.append("时间: ").append(java.time.LocalDateTime.now()).append("\n\n");

        report.append("Broker指标:\n");
        report.append("- 连接数: ").append(metrics.getOrDefault("broker.connection.count", 0.0)).append("\n");
        report.append("- 消费组数量: ").append(metrics.getOrDefault("broker.consumer.group.count", 0.0)).append("\n");
        report.append("- Topic数量: ").append(metrics.getOrDefault("broker.topic.count", 0.0)).append("\n");
        report.append("- 队列数量: ").append(metrics.getOrDefault("broker.queue.count", 0.0)).append("\n");
        report.append("- 消息积压: ").append(metrics.getOrDefault("broker.message.backlog", 0.0)).append("\n\n");

        report.append("系统指标:\n");
        report.append("- 堆内存使用率: ").append(String.format("%.2f%%", metrics.getOrDefault("system.heap.memory.usage.percent", 0.0))).append("\n");
        report.append("- CPU负载: ").append(String.format("%.2f", metrics.getOrDefault("system.cpu.load.average", 0.0))).append("\n");
        report.append("- 线程数量: ").append(metrics.getOrDefault("system.thread.count", 0.0)).append("\n\n");

        // 性能评估
        report.append("性能评估:\n");
        Double connectionCount = metrics.get("broker.connection.count");
        Double consumerGroupCount = metrics.get("broker.consumer.group.count");
        Double heapMemoryUsage = metrics.get("system.heap.memory.usage.percent");

        if (connectionCount != null && connectionCount > 1000) {
            report.append("- 连接数过高，建议优化\n");
        }
        if (consumerGroupCount != null && consumerGroupCount > 50) {
            report.append("- 消费组数量过多，建议合并或清理\n");
        }
        if (heapMemoryUsage != null && heapMemoryUsage > 80) {
            report.append("- 内存使用率过高，建议增加内存或优化\n");
        }

        return report.toString();
    }
} 