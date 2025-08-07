package com.jdktomcat.showcase.caffeine;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

@Slf4j
public class LocalCaffeineCache {

    private static Logger logger = LoggerFactory.getLogger(LocalCaffeineCache.class);

    private static final int EXPIRE_DURATION = 15;

    private static final int REFRESH_DURATION = 10;

    private static final int PARALLELISM = Runtime.getRuntime().availableProcessors() * 4;

    private static final Executor EXECUTOR = new ForkJoinPool(PARALLELISM, ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            (t, e) -> log.error("LocalCache fetch exception from：{}" ,t.getName(), e), true);

    /**
     *
     */
    private final LoadingCache<String, String> localSessionCache = initServiceCache();


    private static Caffeine<Object, Object> caffeineBuilder() {
        return Caffeine.newBuilder()
                .executor(EXECUTOR)
                .expireAfterWrite(EXPIRE_DURATION, TimeUnit.SECONDS)
                .expireAfterAccess(EXPIRE_DURATION, TimeUnit.SECONDS)
                .maximumSize(200)
                .refreshAfterWrite(REFRESH_DURATION, TimeUnit.SECONDS);
    }

    private static LoadingCache<String, String> initServiceCache() {
        BiFunction<String, String, Object> queryConfig = (key, redisCacheKey) -> {
            return null;
        };
        return caffeineBuilder().buildAsync((AsyncCacheLoader<String, String>) (key, executor) ->
                CompletableFuture.supplyAsync(() -> {

                    return null;
                }, executor)
        ).synchronous();
    }

}



