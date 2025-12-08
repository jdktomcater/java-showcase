package com.jdktomcat.showcase.mq.rocketmq.producer.filter.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 消息统计拦截器
 * 统计发送消息的数量、性能指标等
 * 
 * @author ZF-Timmy
 * @version V1.0
 * @date 2024/01/01
 */
@Slf4j
@Component
public class MessageStatisticsInterceptor implements MessageInterceptor {
    
    /**
     * 消息发送总数
     */
    private final AtomicLong totalMessageCount = new AtomicLong(0);
    
    /**
     * 消息发送成功数
     */
    private final AtomicLong successMessageCount = new AtomicLong(0);
    
    /**
     * 消息发送失败数
     */
    private final AtomicLong failedMessageCount = new AtomicLong(0);
    
    /**
     * 消息发送总耗时（毫秒）
     */
    private final LongAdder totalSendTime = new LongAdder();
    
    /**
     * 消息发送平均耗时（毫秒）
     */
    private volatile double averageSendTime = 0.0;
    
    /**
     * 按主题统计的消息数量
     */
    private final Map<String, AtomicLong> topicMessageCount = new ConcurrentHashMap<>();
    
    /**
     * 按标签统计的消息数量
     */
    private final Map<String, AtomicLong> tagMessageCount = new ConcurrentHashMap<>();
    
    /**
     * 消息发送开始时间记录
     */
    private final ThreadLocal<Long> sendStartTime = new ThreadLocal<>();
    
    @Override
    public Message beforeSend(Message message) {
        if (message == null) {
            return null;
        }
        
        // 记录发送开始时间
        sendStartTime.set(System.currentTimeMillis());
        
        // 增加总消息计数
        totalMessageCount.incrementAndGet();
        
        // 按主题统计
        String topic = message.getTopic();
        if (topic != null) {
            topicMessageCount.computeIfAbsent(topic, k -> new AtomicLong(0)).incrementAndGet();
        }
        
        // 按标签统计
        String tags = message.getTags();
        if (tags != null && !tags.isEmpty()) {
            String[] tagArray = tags.split(",");
            for (String tag : tagArray) {
                if (tag != null && !tag.trim().isEmpty()) {
                    tagMessageCount.computeIfAbsent(tag.trim(), k -> new AtomicLong(0)).incrementAndGet();
                }
            }
        }
        
        // 按自动标签统计
        String autoTag = message.getUserProperty("auto_tag");
        if (autoTag != null && !autoTag.isEmpty()) {
            tagMessageCount.computeIfAbsent(autoTag, k -> new AtomicLong(0)).incrementAndGet();
        }
        
        log.debug("开始发送消息: topic={}, tags={}, total_count={}", 
                topic, tags, totalMessageCount.get());
        
        return message;
    }
    
    @Override
    public void afterSend(Message message, SendResult sendResult) {
        // 增加成功消息计数
        successMessageCount.incrementAndGet();
        
        // 计算发送耗时
        Long startTime = sendStartTime.get();
        if (startTime != null) {
            long sendTime = System.currentTimeMillis() - startTime;
            totalSendTime.add(sendTime);
            
            // 计算平均发送时间
            long totalCount = successMessageCount.get();
            if (totalCount > 0) {
                averageSendTime = (double) totalSendTime.sum() / totalCount;
            }
            
            // 清理ThreadLocal
            sendStartTime.remove();
            
            log.debug("消息发送成功: topic={}, msgId={}, send_time={}ms, avg_time={}ms", 
                    message.getTopic(), sendResult.getMsgId(), sendTime, averageSendTime);
        }
        
        // 定期输出统计信息
        if (totalMessageCount.get() % 100 == 0) {
            printStatistics();
        }
    }
    
    @Override
    public void onException(Message message, Throwable throwable) {
        // 增加失败消息计数
        failedMessageCount.incrementAndGet();
        
        // 清理ThreadLocal
        sendStartTime.remove();
        
        log.error("消息发送失败: topic={}, error={}, failed_count={}", 
                message.getTopic(), throwable.getMessage(), failedMessageCount.get());
    }
    
    @Override
    public int getOrder() {
        return 300; // 低优先级，最后执行
    }
    
    /**
     * 获取统计信息
     * 
     * @return 统计信息字符串
     */
    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 消息发送统计信息 ===\n");
        sb.append("总消息数: ").append(totalMessageCount.get()).append("\n");
        sb.append("成功消息数: ").append(successMessageCount.get()).append("\n");
        sb.append("失败消息数: ").append(failedMessageCount.get()).append("\n");
        sb.append("成功率: ").append(calculateSuccessRate()).append("%\n");
        sb.append("平均发送时间: ").append(String.format("%.2f", averageSendTime)).append("ms\n");
        
        sb.append("\n按主题统计:\n");
        topicMessageCount.forEach((topic, count) -> 
                sb.append("  ").append(topic).append(": ").append(count.get()).append("\n"));
        
        sb.append("\n按标签统计:\n");
        tagMessageCount.forEach((tag, count) -> 
                sb.append("  ").append(tag).append(": ").append(count.get()).append("\n"));
        
        return sb.toString();
    }
    
    /**
     * 打印统计信息到日志
     */
    public void printStatistics() {
        log.info("消息发送统计: 总数={}, 成功={}, 失败={}, 成功率={}%, 平均耗时={}ms", 
                totalMessageCount.get(), successMessageCount.get(), failedMessageCount.get(),
                calculateSuccessRate(), String.format("%.2f", averageSendTime));
    }
    
    /**
     * 计算成功率
     * 
     * @return 成功率百分比
     */
    private double calculateSuccessRate() {
        long total = totalMessageCount.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) successMessageCount.get() / total * 100;
    }
    
    /**
     * 重置统计信息
     */
    public void resetStatistics() {
        totalMessageCount.set(0);
        successMessageCount.set(0);
        failedMessageCount.set(0);
        totalSendTime.reset();
        averageSendTime = 0.0;
        topicMessageCount.clear();
        tagMessageCount.clear();
        log.info("消息统计信息已重置");
    }
    
    /**
     * 获取总消息数
     */
    public long getTotalMessageCount() {
        return totalMessageCount.get();
    }
    
    /**
     * 获取成功消息数
     */
    public long getSuccessMessageCount() {
        return successMessageCount.get();
    }
    
    /**
     * 获取失败消息数
     */
    public long getFailedMessageCount() {
        return failedMessageCount.get();
    }
    
    /**
     * 获取平均发送时间
     */
    public double getAverageSendTime() {
        return averageSendTime;
    }
}
