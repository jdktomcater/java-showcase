
package com.jdktomcat.showcase.system.design.fef.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FefServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FefServiceApplication.class, args);
    }
}
