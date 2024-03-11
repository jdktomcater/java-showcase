package com.jdktomcat.demo.redis.template;

import com.jdktomcat.demo.redis.template.component.RedisComponent;
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
@SpringBootApplication
public class DemoRedisRedissonCommandApplication implements CommandLineRunner {

    @Autowired
    private RedisComponent redisComponent;

    public static void main(String[] args) {
        SpringApplication.run(DemoRedisRedissonCommandApplication.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
//        String key = "myzset1";
//        double score = redisComponent.firstScore(key);
//        System.out.println("最小分数值：" + score);
    }
}
