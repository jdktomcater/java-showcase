package com.jdktomcat.showcase.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：
 * @date 2023/11/13
 */
@SpringBootApplication
public class LimiterRedisTokenApplication {

    public static void main(String[] args) {
        SpringApplication.run(LimiterRedisTokenApplication.class, args);
    }

}
