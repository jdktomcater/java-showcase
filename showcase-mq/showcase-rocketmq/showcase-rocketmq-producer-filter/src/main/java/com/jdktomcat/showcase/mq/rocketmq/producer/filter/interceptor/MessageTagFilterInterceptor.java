package com.jdktomcat.showcase.mq.rocketmq.producer.filter.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息标签过滤拦截器
 * 根据消息内容自动添加标签，便于消息分类和过滤
 * 
 * @author ZF-Timmy
 * @version V1.0
 * @date 2024/01/01
 */
@Slf4j
@Component
public class MessageTagFilterInterceptor implements MessageInterceptor {
    
    /**
     * 消息类型与标签的映射关系
     */
    private static final Map<String, String> MESSAGE_TYPE_TAG_MAP = new HashMap<>();
    
    static {
        // 初始化消息类型与标签的映射
        MESSAGE_TYPE_TAG_MAP.put("findDistributionMatchupJob", "distribution");
        MESSAGE_TYPE_TAG_MAP.put("withdraw", "financial");
        MESSAGE_TYPE_TAG_MAP.put("order", "business");
        MESSAGE_TYPE_TAG_MAP.put("alert", "notification");
        MESSAGE_TYPE_TAG_MAP.put("delay", "scheduled");
    }
    
    /**
     * 关键词与标签的映射关系
     */
    private static final Map<String, String> KEYWORD_TAG_MAP = new HashMap<>();
    
    static {
        // 初始化关键词与标签的映射
        KEYWORD_TAG_MAP.put("orderIndex", "order");
        KEYWORD_TAG_MAP.put("merchantOrderId", "merchant");
        KEYWORD_TAG_MAP.put("username", "user");
        KEYWORD_TAG_MAP.put("withdraw", "financial");
        KEYWORD_TAG_MAP.put("alert", "notification");
    }
    
    @Override
    public Message beforeSend(Message message) {
        if (message == null) {
            return null;
        }
        
        try {
            // 获取消息内容
            String messageContent = new String(message.getBody(), StandardCharsets.UTF_8);
            
            // 根据消息内容自动添加标签
            String autoTag = generateAutoTag(messageContent);
            
            if (autoTag != null && !autoTag.isEmpty()) {
                // 如果消息没有标签，则添加自动生成的标签
                if (message.getTags() == null || message.getTags().isEmpty()) {
                    message.putUserProperty("auto_tag", autoTag);
                    log.info("自动添加标签: topic={}, tag={}", message.getTopic(), autoTag);
                } else {
                    // 如果消息已有标签，则添加额外的自动标签
                    String existingTags = message.getTags();
                    String newTags = existingTags + "," + autoTag;
                    message.putUserProperty("auto_tag", autoTag);
                    log.info("添加额外标签: topic={}, existing_tags={}, new_tag={}", 
                            message.getTopic(), existingTags, autoTag);
                }
            }
            
            // 添加消息来源标识
            message.putUserProperty("source", "producer-filter");
            message.putUserProperty("timestamp", String.valueOf(System.currentTimeMillis()));
            
            // 添加消息分类信息
            String messageCategory = categorizeMessage(messageContent);
            if (messageCategory != null) {
                message.putUserProperty("category", messageCategory);
            }
            
        } catch (Exception e) {
            log.warn("消息标签处理异常: {}", e.getMessage());
        }
        
        return message;
    }
    
    @Override
    public void afterSend(Message message, SendResult sendResult) {
        String autoTag = message.getUserProperty("auto_tag");
        if (autoTag != null) {
            log.info("消息发送成功: topic={}, auto_tag={}, msgId={}", 
                    message.getTopic(), autoTag, sendResult.getMsgId());
        }
    }
    
    @Override
    public void onException(Message message, Throwable throwable) {
        String autoTag = message.getUserProperty("auto_tag");
        log.error("消息发送异常: topic={}, auto_tag={}, error={}", 
                message.getTopic(), autoTag, throwable.getMessage(), throwable);
    }
    
    @Override
    public int getOrder() {
        return 200; // 中等优先级
    }
    
    /**
     * 根据消息内容自动生成标签
     * 
     * @param messageContent 消息内容
     * @return 自动生成的标签
     */
    private String generateAutoTag(String messageContent) {
        if (messageContent == null || messageContent.trim().isEmpty()) {
            return null;
        }
        
        // 检查消息类型
        for (Map.Entry<String, String> entry : MESSAGE_TYPE_TAG_MAP.entrySet()) {
            if (messageContent.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // 检查关键词
        for (Map.Entry<String, String> entry : KEYWORD_TAG_MAP.entrySet()) {
            if (messageContent.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // 根据消息内容特征判断
        if (messageContent.contains("JSTWZ")) {
            return "job";
        }
        
        if (messageContent.contains("timmy")) {
            return "user";
        }
        
        if (messageContent.contains("2210171858242079902")) {
            return "order";
        }
        
        return "default";
    }
    
    /**
     * 对消息进行分类
     * 
     * @param messageContent 消息内容
     * @return 消息分类
     */
    private String categorizeMessage(String messageContent) {
        if (messageContent == null || messageContent.trim().isEmpty()) {
            return "unknown";
        }
        
        if (messageContent.contains("findDistributionMatchupJob")) {
            return "distribution_job";
        }
        
        if (messageContent.contains("withdraw")) {
            return "financial_operation";
        }
        
        if (messageContent.contains("orderIndex")) {
            return "order_processing";
        }
        
        if (messageContent.contains("JSTWZ")) {
            return "scheduled_task";
        }
        
        return "general";
    }
}
