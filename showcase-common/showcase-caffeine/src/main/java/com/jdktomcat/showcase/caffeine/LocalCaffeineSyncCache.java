package com.jdktomcat.showcase.caffeine;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.jdktomcat.showcase.caffeine.entry.ByteArrayKey;
import com.jdktomcat.showcase.caffeine.entry.ByteArrayValue;
import com.jdktomcat.showcase.caffeine.entry.MatchupRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LocalCaffeineSyncCache {

    private static final Long REFRESH_DURATION = 5L;

    private static final int MAX_SIZE = 200;

    private static final int PARALLELISM = Runtime.getRuntime().availableProcessors() * 4;

    private static final Executor EXECUTOR = new ForkJoinPool(PARALLELISM, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> log.error("LocalCache fetch exception from：{}", t.getName(), e), true);

    /**
     * 类型撮合规则列表缓存对象
     */
    private static final LoadingCache<Integer, List<MatchupRule>> asyncCollectionCache = initAsyncCollectionCache();

    /**
     * 类型撮合规则列表缓存对象
     */
    private static final LoadingCache<Integer, List<MatchupRule>> syncCollectionCache = initSyncCollectionCache();

    /**
     * 类型撮合规则列表缓存对象
     *
     * @return 缓存对象
     */
    public static LoadingCache<Integer, List<MatchupRule>> initAsyncCollectionCache() {
        return Caffeine.newBuilder().executor(EXECUTOR).maximumSize(MAX_SIZE).expireAfterAccess(REFRESH_DURATION, TimeUnit.SECONDS)
               .removalListener((key, value, cause) -> log.info("type:{} value:{} asyncCollectionCache removalListener cause:{}", key, value, cause))
               .buildAsync((AsyncCacheLoader<Integer, List<MatchupRule>>) (type, executor) -> CompletableFuture.supplyAsync(() -> {
                  log.info("type:{} asyncCollection load data from db!", type);
                  return MatchupRule.getMatchupRules(type);
               }, executor)).synchronous();

    }


    /**
     * 类型撮合规则列表缓存对象
     *
     * @return 缓存对象
     */
    public static LoadingCache<Integer, List<MatchupRule>> initSyncCollectionCache() {
        return Caffeine.newBuilder().executor(EXECUTOR).maximumSize(MAX_SIZE).refreshAfterWrite(REFRESH_DURATION, TimeUnit.SECONDS)
                .removalListener((key, value, cause) -> log.info("type:{} value:{} syncCollectionCache removalListener cause:{}", key, value, cause))
                .build((type) -> {
                    log.info("type:{} syncCollection load data from db!",type);
                    return MatchupRule.getMatchupRules(type);
                });
    }

    /**
     * 获取异步加载信息对象列表
     *
     * @param type 会话标识
     * @return 异步加载信息对象列表
     */
    public static List<MatchupRule> getAsyncList(int type) {
        return asyncCollectionCache.get(type);
    }

    /**
     * 获取同步信息对象列表
     *
     * @param type 会话标识
     * @return 同步信息对象列表
     */
    public static List<MatchupRule> getSyncList(int type) {
        return syncCollectionCache.get(type);
    }
}



