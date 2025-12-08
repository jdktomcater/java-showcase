package com.jdktomcat.showcase.mq.rocketmq.producer.filter.message;

import lombok.Data;

@Data
public class DelayMessage {
    private String merchantOrderId;
}
