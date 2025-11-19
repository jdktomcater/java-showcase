package com.jdktomcat.showcase.redis;

import com.jdktomcat.showcase.redis.component.RedisComponent;
import com.jdktomcat.showcase.redis.task.HandleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootApplication
public class SnowflakeGenerateRedisApplication implements CommandLineRunner {

    @Autowired
    private RedisComponent redisComponent;


    public static void main(String[] args) {
        SpringApplication.run(SnowflakeGenerateRedisApplication.class, args);
    }

    @Override
    public void run(String... args) {
        ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        for (int i = 0; i < 10; i++) {
            executors.submit(new HandleTask(redisComponent));
        }
    }
}

