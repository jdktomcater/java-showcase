package com.jdktomcat.showcase.kafka.producer.message;

import lombok.Data;

@Data
public class DelayMessage {
    private String merchantOrderId;
}
