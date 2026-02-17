package com.jdktomcat.showcase.common.disruptor.producer;

import com.jdktomcat.showcase.common.disruptor.event.OrderEvent;
import com.lmax.disruptor.RingBuffer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderEventProducer {
    private final RingBuffer<OrderEvent> ringBuffer;

    public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void publish(String orderId, BigDecimal price) {
        long sequence = ringBuffer.next();
        try {
            OrderEvent event = ringBuffer.get(sequence);
            event.setOrderId(orderId);
            event.setPrice(price);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}

