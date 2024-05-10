package com.jdktomcat.showcase.redis.redisson.lock;

import jodd.util.StringUtil;
import org.redisson.api.RPermitExpirableSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：
 * @date 2023/11/13
 */
@SpringBootApplication
public class RedissonSemaphoreApplication implements CommandLineRunner {

    @Autowired
    private RedissonClient redissonClient;

    public static void main(String[] args) {
        SpringApplication.run(RedissonSemaphoreApplication.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) throws InterruptedException {
        String semaphoreName = "timmy_semaphore";
        RPermitExpirableSemaphore expirableSemaphore = redissonClient.getPermitExpirableSemaphore(semaphoreName);
        int availablePermits = expirableSemaphore.availablePermits();
        System.out.println("available permit: " + availablePermits);
        boolean setResult = expirableSemaphore.trySetPermits(8);
        System.out.println("set permit result: " + setResult);
        expirableSemaphore.addPermits(5);
        availablePermits = expirableSemaphore.availablePermits();
        System.out.println("available permit: " + availablePermits);
        List<String> permits = new ArrayList<>();
        for (int i = 0; i < availablePermits + 1; i++) {
            String semaphore = expirableSemaphore.tryAcquire(0, 5, TimeUnit.SECONDS);
            if (!StringUtil.isEmpty(semaphore)) {
                permits.add(semaphore);
                System.out.println("acquire permit: " + semaphore);
            } else {
                System.out.println("acquire permit failed");
            }
        }
        permits.forEach(expirableSemaphore::release);
    }
}
