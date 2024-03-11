package com.jdktomcat.demo.snowflake.redis;

import com.jdktomcat.demo.snowflake.redis.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SnowflakeRedisApplication implements CommandLineRunner {

    @Autowired
    private SnowflakeService snowflakeService;

    public static void main(String[] args) {
        SpringApplication.run(SnowflakeRedisApplication.class, args);
    }

    @Override
    public void run(String... args) {
        for(int i=0;i<10000;i++){
            snowflakeService.generate();
        }
    }
}

