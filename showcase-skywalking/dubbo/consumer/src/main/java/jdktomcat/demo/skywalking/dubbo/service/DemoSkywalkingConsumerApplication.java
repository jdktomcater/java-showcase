package jdktomcat.demo.skywalking.dubbo.service;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 */
@EnableDubbo
@SpringBootApplication
public class DemoSkywalkingConsumerApplication {


    public static void main(String[] args) {
        SpringApplication.run(DemoSkywalkingConsumerApplication.class);
    }
}
