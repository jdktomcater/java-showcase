package com.jdktomcat.showcase.limiter.redis.token;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description description
 * @date 2023/11/13
 */
@SpringBootApplication
public class LimiterRedisTokenApplication {

    public static void main(String[] args) {
        SpringApplication.run(LimiterRedisTokenApplication.class, args);
    }

}
