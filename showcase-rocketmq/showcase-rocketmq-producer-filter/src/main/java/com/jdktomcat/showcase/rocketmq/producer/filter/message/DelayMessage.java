package com.jdktomcat.showcase.rocketmq.producer.filter.message;

import lombok.Data;

@Data
public class DelayMessage {
    private String merchantOrderId;
}
