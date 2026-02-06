package com.jdktomcat.showcase.mq.rocketmq.producer.filter.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 消息内容验证拦截器
 * 验证消息格式、大小、内容等
 * 
 * @author ZF-Timmy
 * @version V1.0
 * @date 2024/01/01
 */
@Slf4j
@Component
public class MessageValidationInterceptor implements MessageInterceptor {
    
    /**
     * 最大消息大小限制 (4MB)
     */
    private static final int MAX_MESSAGE_SIZE = 4 * 1024 * 1024;
    
    /**
     * 最小消息大小限制 (1字节)
     */
    private static final int MIN_MESSAGE_SIZE = 1;
    
    @Override
    public Message beforeSend(Message message) {
        if (message == null) {
            log.error("消息不能为空");
            return null;
        }
        
        // 验证消息体
        byte[] body = message.getBody();
        if (body == null || body.length == 0) {
            log.error("消息体不能为空");
            return null;
        }
        
        // 验证消息大小
        if (body.length > MAX_MESSAGE_SIZE) {
            log.error("消息大小超过限制: {} bytes, 最大允许: {} bytes", body.length, MAX_MESSAGE_SIZE);
            return null;
        }
        
        if (body.length < MIN_MESSAGE_SIZE) {
            log.error("消息大小过小: {} bytes, 最小要求: {} bytes", body.length, MIN_MESSAGE_SIZE);
            return null;
        }
        
        // 验证消息内容格式
        String messageContent = new String(body, StandardCharsets.UTF_8);
        if (!isValidMessageContent(messageContent)) {
            log.error("消息内容格式无效: {}", messageContent);
            return null;
        }
        
        // 验证消息属性
        if (message.getTopic() == null || message.getTopic().trim().isEmpty()) {
            log.error("消息主题不能为空");
            return null;
        }
        
        log.info("消息验证通过: topic={}, size={} bytes", message.getTopic(), body.length);
        return message;
    }
    
    @Override
    public void afterSend(Message message, SendResult sendResult) {
        log.info("消息发送成功: topic={}, msgId={}, queueId={}", 
                message.getTopic(), sendResult.getMsgId(), sendResult.getMessageQueue().getQueueId());
    }
    
    @Override
    public void onException(Message message, Throwable throwable) {
        log.error("消息发送异常: topic={}, error={}", 
                message.getTopic(), throwable.getMessage(), throwable);
    }
    
    @Override
    public int getOrder() {
        return 100; // 高优先级，最先执行
    }
    
    /**
     * 验证消息内容格式
     * 
     * @param content 消息内容
     * @return 是否有效
     */
    private boolean isValidMessageContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        
        // 检查是否包含敏感词汇
        String[] sensitiveWords = {"password", "token", "secret", "key"};
        String lowerContent = content.toLowerCase();
        for (String word : sensitiveWords) {
            if (lowerContent.contains(word)) {
                log.warn("消息内容包含敏感词汇: {}", word);
                // 这里可以选择阻止发送或记录日志
            }
        }
        
        // 检查JSON格式（如果消息是JSON格式）
        if (content.trim().startsWith("{") || content.trim().startsWith("[")) {
            try {
                // 简单的JSON格式检查
                if (!content.contains(":") || !content.contains("\"")) {
                    return false;
                }
            } catch (Exception e) {
                log.warn("JSON格式验证失败: {}", e.getMessage());
                return false;
            }
        }
        
        return true;
    }
}
