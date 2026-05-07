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
        executor.initialize();
        return executor;
    }


    @Bean("remoteRequestParallelExecutor")
    public ThreadPoolTaskExecutor remoteRequestParallelExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("remote-request-");
        // 拒绝策略：CallerRuns，避免任务悄然丢失，同时限制突发流量
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 优雅下线：等待已有任务执行完毕
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}


