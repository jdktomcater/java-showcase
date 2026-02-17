package com.jdktomcat.showcase.common.disruptor.factory;

import com.jdktomcat.showcase.common.disruptor.event.OrderEvent;
import com.lmax.disruptor.EventFactory;

/**
 * event factory
 */
public class OrderEventFactory implements EventFactory<OrderEvent> {
    @Override
    public OrderEvent newInstance() {
        return new OrderEvent();
    }
}

