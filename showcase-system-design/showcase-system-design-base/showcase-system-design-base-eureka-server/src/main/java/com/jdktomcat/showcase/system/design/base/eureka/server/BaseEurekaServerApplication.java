package com.jdktomcat.showcase.system.design.base.eureka.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class BaseEurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseEurekaServerApplication.class, args);
    }
}