
package com.jdktomcat.showcase.system.design.pay.mq;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {

//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    public PaymentEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void send(String paymentId) {
//        kafkaTemplate.send("payment-result-topic", paymentId);
//    }
}
