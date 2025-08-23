package com.jdktomcat.showcase.rocketmq.producer.filter.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Arrays;
import java.util.List;

/**
 * 消息加密拦截器
 * 对敏感消息进行加密处理，保护数据安全
 * 
 * @author ZF-Timmy
 * @version V1.0
 * @date 2024/01/01
 */
@Slf4j
@Component
public class MessageEncryptionInterceptor implements MessageInterceptor {
    
    /**
     * 加密密钥（生产环境应该从配置文件或密钥管理服务获取）
     */
    @Value("${demo.rocketmq.encryption.key:defaultEncryptionKey123}")
    private String encryptionKey;
    
    /**
     * 是否启用加密
     */
    @Value("${demo.rocketmq.encryption.enabled:false}")
    private boolean encryptionEnabled;
    
    /**
     * 需要加密的敏感字段关键词
     */
    private static final List<String> SENSITIVE_KEYWORDS = Arrays.asList(
            "password", "token", "secret", "key", "credential", "auth", "private"
    );
    
    /**
     * 需要加密的消息类型
     */
    private static final List<String> ENCRYPTION_MESSAGE_TYPES = Arrays.asList(
            "findDistributionMatchupJob", "withdraw", "financial", "user"
    );
    
    @Override
    public Message beforeSend(Message message) {
        if (message == null || !encryptionEnabled) {
            return message;
        }
        
        try {
            // 检查是否需要加密
            if (shouldEncrypt(message)) {
                // 对消息内容进行加密
                byte[] encryptedBody = encryptMessage(message.getBody());
                if (encryptedBody != null) {
                    message.setBody(encryptedBody);
                    
                    // 添加加密标识
                    message.putUserProperty("encrypted", "true");
                    message.putUserProperty("encryption_algorithm", "AES");
                    message.putUserProperty("original_size", String.valueOf(message.getBody().length));
                    
                    log.info("消息已加密: topic={}, original_size={} bytes, encrypted_size={} bytes", 
                            message.getTopic(), message.getBody().length, encryptedBody.length);
                }
            }
        } catch (Exception e) {
            log.error("消息加密失败: topic={}, error={}", 
                    message.getTopic(), e.getMessage(), e);
            // 加密失败时，可以选择阻止发送或继续发送未加密的消息
            // 这里选择继续发送，但记录错误日志
        }
        
        return message;
    }
    
    @Override
    public void afterSend(Message message, SendResult sendResult) {
        String encrypted = message.getUserProperty("encrypted");
        if ("true".equals(encrypted)) {
            log.info("加密消息发送成功: topic={}, msgId={}", 
                    message.getTopic(), sendResult.getMsgId());
        }
    }
    
    @Override
    public void onException(Message message, Throwable throwable) {
        String encrypted = message.getUserProperty("encrypted");
        log.error("加密消息发送异常: topic={}, encrypted={}, error={}", 
                message.getTopic(), encrypted, throwable.getMessage(), throwable);
    }
    
    @Override
    public int getOrder() {
        return 150; // 在验证之后，标签过滤之前执行
    }
    
    /**
     * 判断消息是否需要加密
     * 
     * @param message 消息对象
     * @return 是否需要加密
     */
    private boolean shouldEncrypt(Message message) {
        if (message == null || message.getBody() == null) {
            return false;
        }
        
        try {
            String messageContent = new String(message.getBody(), StandardCharsets.UTF_8);
            
            // 检查消息类型
            for (String messageType : ENCRYPTION_MESSAGE_TYPES) {
                if (messageContent.contains(messageType)) {
                    return true;
                }
            }
            
            // 检查是否包含敏感关键词
            String lowerContent = messageContent.toLowerCase();
            for (String keyword : SENSITIVE_KEYWORDS) {
                if (lowerContent.contains(keyword)) {
                    return true;
                }
            }
            
            // 检查消息属性中的敏感信息
            String tags = message.getTags();
            if (tags != null) {
                String lowerTags = tags.toLowerCase();
                for (String keyword : SENSITIVE_KEYWORDS) {
                    if (lowerTags.contains(keyword)) {
                        return true;
                    }
                }
            }
            
        } catch (Exception e) {
            log.warn("检查消息是否需要加密时发生异常: {}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * 加密消息内容
     * 
     * @param originalBody 原始消息内容
     * @return 加密后的消息内容
     */
    private byte[] encryptMessage(byte[] originalBody) {
        if (originalBody == null || originalBody.length == 0) {
            return originalBody;
        }
        
        try {
            // 使用AES加密
            SecretKeySpec secretKey = new SecretKeySpec(
                    encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
            
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(originalBody);
            
            // 返回Base64编码的加密结果
            return Base64.getEncoder().encode(encryptedBytes);
            
        } catch (Exception e) {
            log.error("消息加密失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 解密消息内容（用于测试或调试）
     * 
     * @param encryptedBody 加密的消息内容
     * @return 解密后的消息内容
     */
    public byte[] decryptMessage(byte[] encryptedBody) {
        if (encryptedBody == null || encryptedBody.length == 0) {
            return encryptedBody;
        }
        
        try {
            // 解码Base64
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedBody);
            
            // 使用AES解密
            SecretKeySpec secretKey = new SecretKeySpec(
                    encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
            
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            return cipher.doFinal(decodedBytes);
            
        } catch (Exception e) {
            log.error("消息解密失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 检查消息是否已加密
     * 
     * @param message 消息对象
     * @return 是否已加密
     */
    public boolean isMessageEncrypted(Message message) {
        if (message == null) {
            return false;
        }
        return "true".equals(message.getUserProperty("encrypted"));
    }
    
    /**
     * 获取加密算法信息
     * 
     * @param message 消息对象
     * @return 加密算法
     */
    public String getEncryptionAlgorithm(Message message) {
        if (message == null) {
            return null;
        }
        return message.getUserProperty("encryption_algorithm");
    }
}
