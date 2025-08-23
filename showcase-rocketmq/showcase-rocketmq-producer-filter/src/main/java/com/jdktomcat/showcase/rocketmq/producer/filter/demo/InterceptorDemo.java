package com.jdktomcat.showcase.rocketmq.producer.filter.demo;

import com.jdktomcat.showcase.rocketmq.producer.filter.component.MessageProducerComponent;
import com.jdktomcat.showcase.rocketmq.producer.filter.interceptor.MessageInterceptorChain;
import com.jdktomcat.showcase.rocketmq.producer.filter.interceptor.MessageStatisticsInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 拦截器演示类
 * 展示拦截器的各种功能和使用方法
 * 
 * @author ZF-Timmy
 * @version V1.0
 * @date 2024/01/01
 */
@Slf4j
@Component
public class InterceptorDemo implements CommandLineRunner {
    
    @Autowired
    private MessageProducerComponent messageProducerComponent;
    
    @Autowired
    private MessageInterceptorChain interceptorChain;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("=== 开始拦截器演示 ===");
        
        // 等待一下让系统初始化完成
        Thread.sleep(2000);
        
        // 演示拦截器链
        demonstrateInterceptorChain();
        
        // 演示消息发送
        demonstrateMessageSending();
        
        // 演示消息验证
        demonstrateMessageValidation();
        
        // 演示消息标签
        demonstrateMessageTagging();
        
        // 演示统计功能
        demonstrateStatistics();
        
        log.info("=== 拦截器演示完成 ===");
    }
    
    /**
     * 演示拦截器链
     */
    private void demonstrateInterceptorChain() {
        log.info("--- 演示拦截器链 ---");
        
        // 检查拦截器数量
        int interceptorCount = interceptorChain.getInterceptorCount();
        log.info("拦截器数量: {}", interceptorCount);
        
        // 检查是否包含特定拦截器
        boolean hasValidation = interceptorChain.hasInterceptor(
                com.jdktomcat.showcase.rocketmq.producer.filter.interceptor.MessageValidationInterceptor.class);
        boolean hasEncryption = interceptorChain.hasInterceptor(
                com.jdktomcat.showcase.rocketmq.producer.filter.interceptor.MessageEncryptionInterceptor.class);
        boolean hasTagFilter = interceptorChain.hasInterceptor(
                com.jdktomcat.showcase.rocketmq.producer.filter.interceptor.MessageTagFilterInterceptor.class);
        boolean hasStatistics = interceptorChain.hasInterceptor(
                com.jdktomcat.showcase.rocketmq.producer.filter.interceptor.MessageStatisticsInterceptor.class);
        
        log.info("验证拦截器: {}", hasValidation);
        log.info("加密拦截器: {}", hasEncryption);
        log.info("标签过滤拦截器: {}", hasTagFilter);
        log.info("统计拦截器: {}", hasStatistics);
        
        // 获取统计拦截器
        MessageStatisticsInterceptor statisticsInterceptor = 
                interceptorChain.getInterceptor(MessageStatisticsInterceptor.class);
        if (statisticsInterceptor != null) {
            log.info("统计拦截器获取成功");
            log.info("当前统计信息: {}", statisticsInterceptor.getStatistics());
        }
    }
    
    /**
     * 演示消息发送
     */
    private void demonstrateMessageSending() {
        log.info("--- 演示消息发送 ---");
        
        try {
            // 发送不同类型的消息
            String normalMessage = "这是一条普通消息";
            String distributionMessage = "findDistributionMatchupJob: 分配任务";
            String withdrawMessage = "withdraw: 提现订单";
            
            log.info("发送普通消息...");
            messageProducerComponent.sendTelegramMsg(normalMessage, "key1");
            
            log.info("发送分配任务消息...");
            messageProducerComponent.sendDisMessage(distributionMessage, "key2");
            
            log.info("发送提现消息...");
            messageProducerComponent.sendDelayMessage(withdrawMessage, "key3");
            
            // 等待一下让异步处理完成
            Thread.sleep(1000);
            
            // 查看统计信息
            MessageStatisticsInterceptor statisticsInterceptor = 
                    interceptorChain.getInterceptor(MessageStatisticsInterceptor.class);
            if (statisticsInterceptor != null) {
                log.info("发送完成后的统计信息:");
                log.info(statisticsInterceptor.getStatistics());
            }
            
        } catch (Exception e) {
            log.error("消息发送演示失败", e);
        }
    }
    
    /**
     * 演示消息验证
     */
    private void demonstrateMessageValidation() {
        log.info("--- 演示消息验证 ---");
        
        try {
            // 测试空消息
            log.info("测试空消息...");
            try {
                messageProducerComponent.sendTelegramMsg("", "empty-key");
            } catch (Exception e) {
                log.info("空消息被正确拦截: {}", e.getMessage());
            }
            
            // 测试超大消息
            log.info("测试超大消息...");
            StringBuilder largeMessage = new StringBuilder();
            for (int i = 0; i < 1000000; i++) { // 创建大约1MB的消息
                largeMessage.append("这是一条测试消息，用于测试消息大小限制。");
            }
            
            try {
                messageProducerComponent.sendTelegramMsg(largeMessage.toString(), "large-key");
            } catch (Exception e) {
                log.info("超大消息被正确拦截: {}", e.getMessage());
            }
            
        } catch (Exception e) {
            log.error("消息验证演示失败", e);
        }
    }
    
    /**
     * 演示消息标签
     */
    private void demonstrateMessageTagging() {
        log.info("--- 演示消息标签 ---");
        
        try {
            // 发送包含特定关键词的消息
            String userMessage = "username: timmy123, orderIndex: 123456789";
            String financialMessage = "withdraw: 100.00, merchantOrderId: M123456";
            
            log.info("发送用户消息...");
            messageProducerComponent.sendDisMessage(userMessage, "user-key");
            
            log.info("发送金融消息...");
            messageProducerComponent.sendDisMessage(financialMessage, "financial-key");
            
            // 等待处理完成
            Thread.sleep(1000);
            
            // 查看统计信息
            MessageStatisticsInterceptor statisticsInterceptor = 
                    interceptorChain.getInterceptor(MessageStatisticsInterceptor.class);
            if (statisticsInterceptor != null) {
                log.info("标签演示后的统计信息:");
                log.info(statisticsInterceptor.getStatistics());
            }
            
        } catch (Exception e) {
            log.error("消息标签演示失败", e);
        }
    }
    
    /**
     * 演示统计功能
     */
    private void demonstrateStatistics() {
        log.info("--- 演示统计功能 ---");
        
        try {
            // 发送一些消息
            for (int i = 0; i < 5; i++) {
                messageProducerComponent.sendTelegramMsg("演示消息 " + i, "demo-key-" + i);
            }
            
            // 等待处理完成
            Thread.sleep(1000);
            
            // 查看重置前的统计
            MessageStatisticsInterceptor statisticsInterceptor = 
                    interceptorChain.getInterceptor(MessageStatisticsInterceptor.class);
            if (statisticsInterceptor != null) {
                log.info("重置前的统计信息:");
                log.info(statisticsInterceptor.getStatistics());
                
                // 重置统计
                log.info("重置统计信息...");
                statisticsInterceptor.resetStatistics();
                
                log.info("重置后的统计信息:");
                log.info(statisticsInterceptor.getStatistics());
            }
            
        } catch (Exception e) {
            log.error("统计功能演示失败", e);
        }
    }
}
