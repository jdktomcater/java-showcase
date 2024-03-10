package com.jdktomcat.demo.snowflake.generate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SnowflakeGenerateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnowflakeGenerateApplication.class, args);
    }

}

