package com.jdktomcat.showcase.common.disruptor.controller;

import com.jdktomcat.showcase.common.disruptor.producer.OrderEventProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderEventProducer producer;

    public OrderController(OrderEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping(path = "/create")
    public String createOrder(@RequestParam String orderId, @RequestParam BigDecimal price) {
        producer.publish(orderId, price);
        return "Order submitted";
    }
}

