package com.jdktomcat.demo.rocketmq.producer.message;

import lombok.Data;

@Data
public class DelayMessage {
    private String merchantOrderId;
}
