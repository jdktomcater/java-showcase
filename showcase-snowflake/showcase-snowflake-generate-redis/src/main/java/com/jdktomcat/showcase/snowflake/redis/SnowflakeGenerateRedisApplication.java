package com.jdktomcat.showcase.snowflake.redis;

import com.jdktomcat.showcase.snowflake.redis.component.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SnowflakeGenerateRedisApplication implements CommandLineRunner {

    @Autowired
    private RedisComponent redisComponent;


    public static void main(String[] args) {
        SpringApplication.run(SnowflakeGenerateRedisApplication.class, args);
    }

    @Override
    public void run(String... args) {
        String tableName = "t_thirdwithdraworder";
        int count = 0;
        for (int i = 0; i < 100000; i++) {
            long index = redisComponent.getIncrementSeq(tableName);
            boolean success = redisComponent.addSet(index);
            if (!success) {
                count++;
                log.error("序列：{} 出现重复id：{}", i, index);
            }
        }
        log.warn("总共有：{} 条重复记录！", count);
    }
}

