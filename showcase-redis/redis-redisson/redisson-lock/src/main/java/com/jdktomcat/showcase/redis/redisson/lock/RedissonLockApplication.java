package com.jdktomcat.showcase.redis.redisson.lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：redisson lock
 * @date 2023/11/13
 */
@SpringBootApplication
public class RedissonLockApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedissonLockApplication.class, args);
    }
}
