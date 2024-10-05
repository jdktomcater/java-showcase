package com.jdktomcat.showcase.prometheus.druid;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.jdktomcat.showcase.prometheus.druid.mapper")
@SpringBootApplication
public class PrometheusDruidApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrometheusDruidApplication.class, args);
    }
}
