package com.jdktomcat.showcase.redis.redisson.lock;

import jakarta.annotation.Resource;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：
 * @date 2023/11/13
 */
@SpringBootApplication
public class RedissonCommandApplication implements CommandLineRunner {

    @Resource
    private RedissonClient redissonClient;

    public static void main(String[] args) {
        SpringApplication.run(RedissonCommandApplication.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        // 数据1
        String key1 = "1031209264749744148";
        String key2 = "902925131023741006";
        String jwtToken = "1";
        String keyNew1 = "AUTH:SERVER:TOKEN:" + key1;
        String keyNew2 = "AUTH:SERVER:TOKEN:" + key2;
        RMap<String,Object> tokenMap1 = redissonClient.getMap(keyNew1);
        RMap<String,Object> tokenMap2 = redissonClient.getMap(keyNew2);
        if (!tokenMap1.isEmpty()) {
            tokenMap1.clear();
        }
        if (!tokenMap2.isEmpty()) {
            tokenMap2.clear();
        }
        Object value1 = tokenMap1.put(jwtToken, 12);
        System.out.println(value1);
        Object value2 = tokenMap2.put(jwtToken, 12);
        System.out.println(value2);

    }
}
