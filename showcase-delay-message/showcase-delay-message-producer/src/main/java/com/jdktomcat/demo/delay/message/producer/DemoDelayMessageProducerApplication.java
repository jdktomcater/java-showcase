package com.jdktomcat.demo.delay.message.producer;

import com.jdktomcat.demo.delay.message.producer.component.MessageProducerComponent;
import com.jdktomcat.demo.delay.message.producer.component.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class DemoDelayMessageProducerApplication implements CommandLineRunner {

    @Autowired
    private MessageProducerComponent messageProducerComponent;

    @Autowired
    private RedisComponent redisComponent;


    public static void main(String[] args) {
        SpringApplication.run(DemoDelayMessageProducerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Send string
        int i = 0;
        int delay = 30;
        while (i < 1) {
            boolean result = redisComponent.setIfAbsent("delay_message_key_" + i, "delay_message_value_" + i, delay, TimeUnit.SECONDS);
            log.info("setIfAbsent result:{}", result);
            SendResult sendResult = messageProducerComponent.send("delay_message_key_" + i, delay);
            log.info("syncSend to sendResult:{}", sendResult);
            i++;
        }
    }
}

