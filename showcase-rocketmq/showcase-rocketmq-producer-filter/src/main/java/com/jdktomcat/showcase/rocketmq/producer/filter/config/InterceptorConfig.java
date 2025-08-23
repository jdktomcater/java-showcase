package com.jdktomcat.showcase.rocketmq.producer.filter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 拦截器配置类
 * 
 * @author ZF-Timmy
 * @version V1.0
 * @date 2024/01/01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "demo.rocketmq.interceptor")
public class InterceptorConfig {
    
    /**
     * 是否启用消息验证拦截器
     */
    private boolean validationEnabled = true;
    
    /**
     * 是否启用消息加密拦截器
     */
    private boolean encryptionEnabled = false;
    
    /**
     * 是否启用消息标签过滤拦截器
     */
    private boolean tagFilterEnabled = true;
    
    /**
     * 是否启用消息统计拦截器
     */
    private boolean statisticsEnabled = true;
    
    /**
     * 消息大小限制配置
     */
    private MessageSizeConfig messageSize = new MessageSizeConfig();
    
    /**
     * 加密配置
     */
    private EncryptionConfig encryption = new EncryptionConfig();
    
    /**
     * 统计配置
     */
    private StatisticsConfig statistics = new StatisticsConfig();
    
    /**
     * 消息大小配置
     */
    @Data
    public static class MessageSizeConfig {
        /**
         * 最大消息大小（字节）
         */
        private int maxSize = 4 * 1024 * 1024; // 4MB
        
        /**
         * 最小消息大小（字节）
         */
        private int minSize = 1;
    }
    
    /**
     * 加密配置
     */
    @Data
    public static class EncryptionConfig {
        /**
         * 加密密钥
         */
        private String key = "defaultEncryptionKey123";
        
        /**
         * 加密算法
         */
        private String algorithm = "AES";
        
        /**
         * 需要加密的消息类型
         */
        private String[] messageTypes = {"findDistributionMatchupJob", "withdraw", "financial", "user"};
        
        /**
         * 敏感关键词
         */
        private String[] sensitiveKeywords = {"password", "token", "secret", "key", "credential", "auth", "private"};
    }
    
    /**
     * 统计配置
     */
    @Data
    public static class StatisticsConfig {
        /**
         * 统计信息输出间隔（消息数量）
         */
        private int outputInterval = 100;
        
        /**
         * 是否启用详细统计
         */
        private boolean detailedEnabled = true;
        
        /**
         * 统计信息保留时间（小时）
         */
        private int retentionHours = 24;
    }
}
