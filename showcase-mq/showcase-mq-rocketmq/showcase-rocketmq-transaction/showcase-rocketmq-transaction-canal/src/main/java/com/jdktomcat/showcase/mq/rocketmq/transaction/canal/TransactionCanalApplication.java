package com.jdktomcat.showcase.mq.rocketmq.transaction.canal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 事务消息生产者示例
 */
@Slf4j
@SpringBootApplication
public class TransactionCanalApplication {
    public static void main( String[] args ) {
        SpringApplication.run(TransactionCanalApplication.class, args);
    }
}
