package com.jdktomcat.demo.redis.delay.keyspace.notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：
 * @date 2023/11/13
 */
@EnableScheduling
@SpringBootApplication
public class RedisDelayKeyspaceNotifyApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedisDelayKeyspaceNotifyApplication.class, args);
    }
}
