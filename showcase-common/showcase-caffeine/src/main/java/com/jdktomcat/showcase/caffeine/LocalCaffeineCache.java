package com.jdktomcat.showcase.caffeine;

import com.github.benmanes.caffeine.cache.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LocalCaffeineCache {

    private static final Logger logger = LoggerFactory.getLogger(LocalCaffeineCache.class);

    private static final int REFRESH_DURATION = 15;

    private static final int PARALLELISM = Runtime.getRuntime().availableProcessors() * 4;

    private static final Executor EXECUTOR = new ForkJoinPool(PARALLELISM, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> log.error("LocalCache fetch exception from：{}", t.getName(), e), true);

    /**
     * 本地缓存信息
     */
    public static final LoadingCache<String, String> localSessionCache = initServiceCache();

    /**
     * 本地缓存策略
     *
     * @return 缓存策略
     */
    private static Caffeine<Object, Object> caffeineBuilder() {
        return Caffeine.newBuilder().removalListener((o, o2, removalCause) -> logger.info("key:{}, value:{}", o, o2)).executor(EXECUTOR).maximumSize(200).refreshAfterWrite(REFRESH_DURATION, TimeUnit.SECONDS);
    }

    /**
     * 本地缓存初始化
     *
     * @return 缓存
     */
    private static LoadingCache<String, String> initServiceCache() {
        return caffeineBuilder().buildAsync((AsyncCacheLoader<String, String>) (key, executor) -> CompletableFuture.supplyAsync(() -> {
            logger.warn("本地缓存key:{}已经失效，需要异步加载！", key);
            return "" + System.currentTimeMillis();
        }, executor)).synchronous();
    }
}



