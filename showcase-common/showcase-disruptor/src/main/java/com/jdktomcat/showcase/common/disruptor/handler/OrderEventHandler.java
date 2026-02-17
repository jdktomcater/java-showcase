package com.jdktomcat.showcase.common.disruptor.handler;

import com.jdktomcat.showcase.common.disruptor.event.OrderEvent;
import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderEventHandler implements EventHandler<OrderEvent> {

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        log.info("Processing order: {} price: {}", event.getOrderId(), event.getPrice());
        // 模拟业务处理
    }
}
