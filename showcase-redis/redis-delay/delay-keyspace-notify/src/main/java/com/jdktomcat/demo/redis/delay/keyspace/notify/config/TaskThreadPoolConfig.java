package com.jdktomcat.demo.redis.delay.keyspace.notify.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
@Data
public class TaskThreadPoolConfig {

    @Value("${spring.task.pool.core:20}")
    private int corePoolSize;
    @Value("${spring.task.pool.max:100}")
    private int maxPoolSize;
    @Value("${spring.task.pool.queue:1000}")
    private int queueCapacity;
    @Value("${spring.task.pool.keepAlive:180}")
    private int keepAliveSeconds;
    @Value("${spring.task.pool.prefix:taskAsyncPool}")
    private String threadNamePrefix;

    @Bean
    public ThreadPoolTaskExecutor taskAsyncPool() {
        // 设置监听使用的线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}