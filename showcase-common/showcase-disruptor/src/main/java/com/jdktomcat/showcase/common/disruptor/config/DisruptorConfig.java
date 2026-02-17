package com.jdktomcat.showcase.common.disruptor.config;

import com.jdktomcat.showcase.common.disruptor.event.OrderEvent;
import com.jdktomcat.showcase.common.disruptor.factory.OrderEventFactory;
import com.jdktomcat.showcase.common.disruptor.handler.OrderEventHandler;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class DisruptorConfig {

    @Bean
    public Disruptor<OrderEvent> disruptor() {
        int bufferSize = 1024; // 必须是 2 的幂
        Disruptor<OrderEvent> disruptor = new Disruptor<>(new OrderEventFactory(), bufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new BusySpinWaitStrategy());
        disruptor.handleEventsWith(new OrderEventHandler());
        disruptor.start();
        return disruptor;
    }

    @Bean
    public RingBuffer<OrderEvent> ringBuffer(Disruptor<OrderEvent> disruptor) {
        return disruptor.getRingBuffer();
    }
}
