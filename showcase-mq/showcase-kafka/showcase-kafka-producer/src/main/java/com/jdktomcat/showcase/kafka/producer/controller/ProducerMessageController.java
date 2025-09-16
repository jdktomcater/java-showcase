package com.jdktomcat.showcase.kafka.producer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class ProducerMessageController {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;


    @RequestMapping("/send")
    public String sendMessage(@RequestParam("key") String key, @RequestParam("value") String value) {
        try {
            for (int i = 0; i < 3; i++) {
                kafkaTemplate.send("test_topic", 0, key + i, value + i);
            }
            return "发送成功";
        } catch (Exception e) {
            log.error("Failed to send message caused by {}", e.getMessage());
            return "发送失败";
        }
    }
}