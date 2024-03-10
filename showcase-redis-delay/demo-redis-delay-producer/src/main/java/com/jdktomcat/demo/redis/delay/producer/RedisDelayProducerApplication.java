package com.jdktomcat.demo.redis.delay.producer;

import com.jdktomcat.demo.redis.delay.producer.component.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：
 * @date 2023/11/13
 */
@Slf4j
@SpringBootApplication
public class RedisDelayProducerApplication implements CommandLineRunner {

    @Autowired
    private RedisComponent redisComponent;

    public static void main(String[] args) {
        SpringApplication.run(RedisDelayProducerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        int i = 0;
        while (i++ < 1000000) {
            String key = "key"+i;
            String value = RandomStringUtils.randomAscii(10);
            int timeout = RandomUtils.nextInt(1,10);
            boolean result = redisComponent.setString(key, value, timeout);
            log.info("保存信息 key:{} value:{} timeout:{} result:{}", key,value,timeout,result);
        }
    }
}
