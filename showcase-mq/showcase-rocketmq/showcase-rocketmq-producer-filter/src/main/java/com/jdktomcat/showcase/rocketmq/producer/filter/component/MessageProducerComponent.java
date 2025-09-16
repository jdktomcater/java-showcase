package com.jdktomcat.showcase.rocketmq.producer.filter.component;

import com.jdktomcat.showcase.rocketmq.producer.filter.interceptor.MessageInterceptorChain;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：消息生产者服务组件
 * @date 2023/11/8
 */
@Slf4j
@Component
public class MessageProducerComponent {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    @Autowired
    private MessageInterceptorChain interceptorChain;

    @Value("${demo.rocketmq.topic}")
    private String topic;

    @Value("${demo.rocketmq.dis.topic}")
    private String disTopic;

    @Value("${demo.rocketmq.delay.topic}")
    private String delayTopic;

    /**
     * 发送消息
     *
     * @param message 消息
     * @return 发送结果
     */
    public SendResult sendTelegramMsg(String message, String hashKey) {
        return sendMessageWithInterceptor(topic, message, hashKey);
    }

    /**
     * 发送消息
     *
     * @param message 消息
     * @return 发送结果
     */
    public SendResult sendDisMessage(String message, String hashKey) {
        return sendMessageWithInterceptor(disTopic, message, hashKey);
    }

    /**
     * 发送消息
     *
     * @param message 消息
     * @return 发送结果
     */
    public SendResult sendDelayMessage(String message, String hashKey) {
        return sendMessageWithInterceptor(delayTopic, message, hashKey);
    }
    
    /**
     * 带拦截器的消息发送方法
     *
     * @param topic 主题
     * @param messageContent 消息内容
     * @param hashKey 哈希键
     * @return 发送结果
     */
    private SendResult sendMessageWithInterceptor(String topic, String messageContent, String hashKey) {
        try {
            // 创建消息对象
            Message message = new Message(topic, messageContent.getBytes());
            
            // 执行发送前拦截器
            Message processedMessage = interceptorChain.beforeSend(message);
            if (processedMessage == null) {
                log.warn("消息被拦截器阻止发送: topic={}, content={}", topic, messageContent);
                throw new RuntimeException("消息被拦截器阻止发送");
            }
            
            // 发送消息
            SendResult sendResult = rocketMQTemplate.syncSendOrderly(topic, processedMessage, hashKey);
            
            // 执行发送后拦截器
            interceptorChain.afterSend(processedMessage, sendResult);
            
            log.debug("消息发送成功: topic={}, msgId={}, hashKey={}", 
                    topic, sendResult.getMsgId(), hashKey);
            
            return sendResult;
            
        } catch (Exception e) {
            log.error("消息发送失败: topic={}, content={}, hashKey={}, error={}", 
                    topic, messageContent, hashKey, e.getMessage(), e);
            
            // 执行异常拦截器
            try {
                Message errorMessage = new Message(topic, messageContent.getBytes());
                interceptorChain.onException(errorMessage, e);
            } catch (Exception ex) {
                log.error("执行异常拦截器失败: {}", ex.getMessage(), ex);
            }
            
            throw new RuntimeException("消息发送失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取拦截器链
     *
     * @return 拦截器链
     */
    public MessageInterceptorChain getInterceptorChain() {
        return interceptorChain;
    }
}
