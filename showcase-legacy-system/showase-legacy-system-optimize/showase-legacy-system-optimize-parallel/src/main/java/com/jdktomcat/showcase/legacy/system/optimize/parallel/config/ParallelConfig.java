package com.jdktomcat.showcase.legacy.system.optimize.parallel.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class ParallelConfig {

    @Bean
    public ExecutorService parallelExecutor() {
        return new ThreadPoolExecutor(
                10,50,
                5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                r -> new Thread(r, "parallelExecutor-thread"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

}
