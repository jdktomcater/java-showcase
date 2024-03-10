package com.jdktomcat.demo.rocketmq.consumer.config.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.ServiceState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述 消费者下线配置
 * @date 2024-02-2024/2/25 下午 3:56
 */
@Component
@Slf4j
public class MqElegantOfflineListener implements ApplicationListener<ContextClosedEvent> {

    @Value("${rocketmq.consumer.elegant:true}")
    private boolean elegant;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent( ContextClosedEvent event) {
        log.info("接收到服务下线事件：{} 时间：{}", event, event.getTimestamp());
        try {
            if (!elegant) {
                log.warn("消息未开启优雅下线，可能会导致消息消费异常，进而在发版过程中影响业务！请开启");
                return;
            }
            ApplicationContext applicationContext = event.getApplicationContext();
            long startProducer = System.currentTimeMillis();
            log.info("优雅关闭生产者端实例上下文：{}，开始于：{}", applicationContext.getDisplayName(), startProducer);
            Map<String, TransactionMQProducer> producerMap = applicationContext.getBeansOfType(TransactionMQProducer.class, true, false);
            producerMap.forEach((key, producer) -> {
                long startSub = System.currentTimeMillis();
                ServiceState serviceState = producer.getDefaultMQProducerImpl().getServiceState();
                log.info("优雅关闭生产者端生产实例:{}，开始于：{} 当前服务状态：{}", key, startSub, serviceState.name());
                if (ServiceState.RUNNING == serviceState) {
                    producer.shutdown();
                } else {
                    log.info("优雅关闭生产者生产实例:{}，已经不再生产消息, 当前服务状态：{}", key, serviceState.name());
                }
                long endSub = System.currentTimeMillis();
                log.info("优雅关闭生产者生产实例:{}，结束于:{} 耗時:{}ms", key, endSub, endSub - startSub);
            });
            long endProducer = System.currentTimeMillis();
            log.info("优雅关闭生产者端实例上下文：{}，结束于：{} 耗时:{}", applicationContext.getDisplayName(), endProducer, endProducer - startProducer);

            long startConsumer = System.currentTimeMillis();
            log.info("优雅关闭消费者端消费实例上下文：{}，开始于：{}", applicationContext.getDisplayName(), startConsumer);
            Map<String, DefaultMQPushConsumer> consumerMap = applicationContext.getBeansOfType(DefaultMQPushConsumer.class, true, false);
            consumerMap.forEach((key, consumer) -> {
                long startSub = System.currentTimeMillis();
                ServiceState serviceState = consumer.getDefaultMQPushConsumerImpl().getServiceState();
                log.info("优雅关闭消费者端消费实例:{}，开始于：{} 当前服务状态：{}", key, startSub, serviceState.name());
                if (ServiceState.RUNNING == serviceState) {
                    consumer.shutdown();
                } else {
                    log.info("优雅关闭消费者端消费实例:{}，已经不再消费消息, 当前服务状态：{}", key, serviceState.name());
                }
                long endSub = System.currentTimeMillis();
                log.info("优雅关闭消费者端消费实例:{}，结束于:{} 耗時:{}ms", key, endSub, endSub - startSub);
            });
            // 暂停5s消化已经拉取的消息
            if(!producerMap.isEmpty() || !consumerMap.isEmpty()){
                TimeUnit.SECONDS.sleep(5);
            }
            long endConsumer = System.currentTimeMillis();
            log.info("优雅关闭消费者端消费实例上下文:{}, 完成于：{},耗时:{}", applicationContext.getDisplayName(), endConsumer, endConsumer - startConsumer);
        } catch (Exception ex) {
            log.error("消息消费者优雅关闭发生异常：", ex);
        }
    }
}
