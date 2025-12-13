package com.jdktomcat.showcase.common.threadpool;

import org.dromara.dynamictp.spring.annotation.EnableDynamicTp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDynamicTp
@SpringBootApplication
public class ThreadpoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThreadpoolApplication.class, args);
    }
}
