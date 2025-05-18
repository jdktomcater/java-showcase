package com.jdktomcat.showcase.nacos.application;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

/**
 * Hello world!
 */
@Slf4j
@SpringBootApplication
public class ShowcaseNacosApplication {
    public static void main(String[] args) {
        // 系统变量，控制MQ消费者
        if(StringUtils.isBlank(System.getProperty("rocketmq.consumer.active"))){
            System.setProperty("rocketmq.consumer.active", "on");
        }
        SpringApplication.run(ShowcaseNacosApplication.class, args);
    }
}
