package com.jdktomcat.showcase.mq.rocketmq.transaction.consumer.entry;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {

    private String merchantOrderId;

    private Integer merchantId;

    private BigDecimal amount;

}
