package com.jdktomcat.showcase.common.threadpool.executor;

import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.core.support.DynamicTp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class ThreadPoolConfig {

    @DynamicTp
    @Bean("matchUpTaskAsyncPool")
    public Executor matchUpTaskAsyncPool() {
        // 设置监听使用的线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(2);
        executor.setThreadNamePrefix("thread-pool-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        executor.setRejectedExecutionHandler(new CallerRunsPolicyWithReport());
        executor.initialize();
        return executor;
    }
}


