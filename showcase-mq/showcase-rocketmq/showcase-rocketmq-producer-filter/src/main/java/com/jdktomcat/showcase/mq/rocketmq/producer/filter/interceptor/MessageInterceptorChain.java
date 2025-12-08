package com.jdktomcat.showcase.mq.rocketmq.producer.filter.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息拦截器链管理器
 * 负责按优先级顺序执行所有拦截器
 * 
 * @author ZF-Timmy
 * @version V1.0
 * @date 2024/01/01
 */
@Slf4j
@Component
public class MessageInterceptorChain {
    
    @Autowired
    private List<MessageInterceptor> interceptors;
    
    /**
     * 排序后的拦截器列表
     */
    private List<MessageInterceptor> sortedInterceptors;
    
    @PostConstruct
    public void init() {
        // 按优先级排序拦截器
        sortedInterceptors = interceptors.stream()
                .sorted(Comparator.comparingInt(MessageInterceptor::getOrder))
                .collect(Collectors.toList());
        
        log.info("消息拦截器链初始化完成，共加载 {} 个拦截器", sortedInterceptors.size());
        
        // 输出拦截器加载信息
        for (MessageInterceptor interceptor : sortedInterceptors) {
            log.info("拦截器: {} (优先级: {})", 
                    interceptor.getClass().getSimpleName(), interceptor.getOrder());
        }
    }
    
    /**
     * 执行消息发送前拦截
     * 
     * @param message 待发送的消息
     * @return 处理后的消息，如果返回null则阻止发送
     */
    public Message beforeSend(Message message) {
        if (message == null || sortedInterceptors == null) {
            return message;
        }
        
        Message processedMessage = message;
        
        try {
            for (MessageInterceptor interceptor : sortedInterceptors) {
                try {
                    log.debug("执行拦截器: {} - beforeSend", 
                            interceptor.getClass().getSimpleName());
                    
                    processedMessage = interceptor.beforeSend(processedMessage);
                    
                    // 如果拦截器返回null，则阻止发送
                    if (processedMessage == null) {
                        log.warn("拦截器 {} 阻止了消息发送", 
                                interceptor.getClass().getSimpleName());
                        return null;
                    }
                    
                } catch (Exception e) {
                    log.error("拦截器 {} 执行异常: {}", 
                            interceptor.getClass().getSimpleName(), e.getMessage(), e);
                    // 拦截器异常不影响其他拦截器执行
                }
            }
        } catch (Exception e) {
            log.error("拦截器链执行异常: {}", e.getMessage(), e);
        }
        
        return processedMessage;
    }
    
    /**
     * 执行消息发送后拦截
     * 
     * @param message 已发送的消息
     * @param sendResult 发送结果
     */
    public void afterSend(Message message, SendResult sendResult) {
        if (message == null || sendResult == null || sortedInterceptors == null) {
            return;
        }
        
        try {
            for (MessageInterceptor interceptor : sortedInterceptors) {
                try {
                    log.debug("执行拦截器: {} - afterSend", 
                            interceptor.getClass().getSimpleName());
                    
                    interceptor.afterSend(message, sendResult);
                    
                } catch (Exception e) {
                    log.error("拦截器 {} 执行异常: {}", 
                            interceptor.getClass().getSimpleName(), e.getMessage(), e);
                    // 拦截器异常不影响其他拦截器执行
                }
            }
        } catch (Exception e) {
            log.error("拦截器链执行异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 执行消息发送异常拦截
     * 
     * @param message 发送失败的消息
     * @param throwable 异常信息
     */
    public void onException(Message message, Throwable throwable) {
        if (message == null || throwable == null || sortedInterceptors == null) {
            return;
        }
        
        try {
            for (MessageInterceptor interceptor : sortedInterceptors) {
                try {
                    log.debug("执行拦截器: {} - onException", 
                            interceptor.getClass().getSimpleName());
                    
                    interceptor.onException(message, throwable);
                    
                } catch (Exception e) {
                    log.error("拦截器 {} 执行异常: {}", 
                            interceptor.getClass().getSimpleName(), e.getMessage(), e);
                    // 拦截器异常不影响其他拦截器执行
                }
            }
        } catch (Exception e) {
            log.error("拦截器链执行异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取所有拦截器
     * 
     * @return 拦截器列表
     */
    public List<MessageInterceptor> getInterceptors() {
        return sortedInterceptors;
    }
    
    /**
     * 获取指定类型的拦截器
     * 
     * @param interceptorClass 拦截器类型
     * @param <T> 拦截器类型参数
     * @return 指定类型的拦截器，如果不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public <T extends MessageInterceptor> T getInterceptor(Class<T> interceptorClass) {
        if (sortedInterceptors == null) {
            return null;
        }
        
        return (T) sortedInterceptors.stream()
                .filter(interceptor -> interceptorClass.isInstance(interceptor))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 检查是否包含指定类型的拦截器
     * 
     * @param interceptorClass 拦截器类型
     * @return 是否包含
     */
    public boolean hasInterceptor(Class<? extends MessageInterceptor> interceptorClass) {
        if (sortedInterceptors == null) {
            return false;
        }
        
        return sortedInterceptors.stream()
                .anyMatch(interceptor -> interceptorClass.isInstance(interceptor));
    }
    
    /**
     * 获取拦截器数量
     * 
     * @return 拦截器数量
     */
    public int getInterceptorCount() {
        return sortedInterceptors != null ? sortedInterceptors.size() : 0;
    }
}
