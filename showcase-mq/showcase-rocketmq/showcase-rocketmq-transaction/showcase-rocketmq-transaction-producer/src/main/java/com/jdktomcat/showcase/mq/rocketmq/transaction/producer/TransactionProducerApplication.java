package com.jdktomcat.showcase.mq.rocketmq.transaction.producer;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 事务消息生产者示例
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.jdktomcat.showcase.mq.rocketmq.transaction.producer.mapper")
public class TransactionProducerApplication{
    public static void main( String[] args ) {
        SpringApplication.run(TransactionProducerApplication.class, args);
    }
}
