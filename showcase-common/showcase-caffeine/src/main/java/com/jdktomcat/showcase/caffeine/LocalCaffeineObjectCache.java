package com.jdktomcat.showcase.caffeine;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.jdktomcat.showcase.caffeine.entry.ByteArrayKey;
import com.jdktomcat.showcase.caffeine.entry.ByteArrayValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LocalCaffeineObjectCache {

    private static final int REFRESH_DURATION = 3;

    private static final int MAX_SIZE = 200;

    private static final int PARALLELISM = Runtime.getRuntime().availableProcessors() * 4;

    private static final Executor EXECUTOR = new ForkJoinPool(PARALLELISM, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> log.error("LocalCache fetch exception from：{}", t.getName(), e), true);

    /**
     * 缓存对象
     */
    public static final LoadingCache<ByteArrayKey, Object> localSessionCache = initSessionCache();

    /**
     * 缓存初始化
     *
     * @return 缓存
     */
    private static LoadingCache<ByteArrayKey, Object> initSessionCache() {
        return Caffeine.newBuilder().executor(EXECUTOR)
                .maximumSize(MAX_SIZE)
                .refreshAfterWrite(REFRESH_DURATION, TimeUnit.SECONDS)
                .removalListener((key, value, cause) -> log.info("initSessionCache removalListener key:{} value:{} cause:{}", key, value, cause))
                .buildAsync((AsyncCacheLoader<ByteArrayKey, Object>) (key, executor) -> CompletableFuture.supplyAsync(() -> {
                    log.info("异步从redis中查询数据，key:{}", key);
                    String sessionId = new String(key.getKey());
                    String value = sessionId + "-" + System.currentTimeMillis();
                    return new ByteArrayValue(value.getBytes());
                }, executor)).synchronous();
    }


    /**
     * 获取会话信息对象
     *
     * @param key 会话标识
     * @return 会话信息
     */
    public static Object getSessionObject(byte[] key) {
        return localSessionCache.get(new ByteArrayKey(key));
    }


    public static byte[] getKeyBytes(Object key){
        if (key instanceof String) {
            String redisKey = "redis-" + key;
            return redisKey.getBytes();
        } else {
            return SerializationUtils.serialize((Serializable) key);
        }
    }
}



