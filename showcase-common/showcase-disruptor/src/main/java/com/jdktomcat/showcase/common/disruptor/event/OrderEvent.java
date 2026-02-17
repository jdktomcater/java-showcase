package com.jdktomcat.showcase.common.disruptor.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderEvent {

    private String orderId;

    private BigDecimal price;
}
