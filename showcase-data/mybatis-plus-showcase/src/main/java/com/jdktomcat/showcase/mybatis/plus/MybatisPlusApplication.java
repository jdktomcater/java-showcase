package com.jdktomcat.showcase.mybatis.plus;

import com.jdktomcat.showcase.mybatis.plus.mapper.DepositOrderLogMapper;
import com.jdktomcat.showcase.mybatis.plus.task.InsertTask;
import jakarta.annotation.Resource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 启动类
 */
@SpringBootApplication
@MapperScan("com.jdktomcat.showcase.mybatis.plus.mapper")
public class MybatisPlusApplication implements CommandLineRunner {

    @Resource
    private DepositOrderLogMapper depositOrderLogMapper;

    public static void main(String[] args) {

        SpringApplication.run(MybatisPlusApplication.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 40, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
        for (int i = 0; i < 10000; i++) {
            threadPoolExecutor.submit(new InsertTask(depositOrderLogMapper));
        }
    }
}