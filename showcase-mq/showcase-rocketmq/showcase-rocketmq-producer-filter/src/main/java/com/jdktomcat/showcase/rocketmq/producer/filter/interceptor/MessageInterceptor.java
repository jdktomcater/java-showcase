package com.jdktomcat.showcase.rocketmq.producer.filter.interceptor;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * 消息拦截器接口
 * 用于在消息发送前后进行拦截处理
 * 
 * @author ZF-Timmy
 * @version V1.0
 * @date 2024/01/01
 */
public interface MessageInterceptor {
    
    /**
     * 消息发送前拦截
     * 
     * @param message 待发送的消息
     * @return 处理后的消息，如果返回null则阻止发送
     */
    Message beforeSend(Message message);
    
    /**
     * 消息发送后拦截
     * 
     * @param message 已发送的消息
     * @param sendResult 发送结果
     */
    void afterSend(Message message, SendResult sendResult);
    
    /**
     * 消息发送异常拦截
     * 
     * @param message 发送失败的消息
     * @param throwable 异常信息
     */
    void onException(Message message, Throwable throwable);
    
    /**
     * 获取拦截器优先级，数值越小优先级越高
     * 
     * @return 优先级数值
     */
    default int getOrder() {
        return 0;
    }
}
