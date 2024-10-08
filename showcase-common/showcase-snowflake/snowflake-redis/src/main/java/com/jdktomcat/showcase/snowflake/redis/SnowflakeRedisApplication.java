package com.jdktomcat.showcase.snowflake.redis;

import com.alibaba.fastjson.JSONObject;
import com.jdktomcat.showcase.snowflake.redis.service.SnowflakeService;
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
        String code = "200";
        JSONObject jsonObject = JSONObject.parseObject(code);
        System.out.println(jsonObject);
    }
}

