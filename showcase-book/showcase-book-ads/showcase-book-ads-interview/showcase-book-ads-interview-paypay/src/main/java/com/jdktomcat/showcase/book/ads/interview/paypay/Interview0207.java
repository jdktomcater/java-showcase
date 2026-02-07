package com.jdktomcat.showcase.book.ads.interview.paypay;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Interview0207 {

    public static final Long TIME_WINDOW = 10000L;

    public static final ConcurrentHashMap<String, Long> REQUEST_MAP = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    private static final int REFRESH_DURATION = 5;

    private static final int MAX_SIZE = 1000000;

    /**
     * caffeine
     */
    public static final Cache<String, Boolean> REQUEST_CACHE = Caffeine.newBuilder().maximumSize(MAX_SIZE).expireAfterWrite(REFRESH_DURATION, TimeUnit.SECONDS).build();

    static {
        cleaner.scheduleAtFixedRate(() -> REQUEST_MAP.forEach((k, v) -> {
            if (System.currentTimeMillis() - v > TIME_WINDOW) {
                // CAS delete safe
                REQUEST_MAP.remove(k, v);
            }
        }), 10, 10, TimeUnit.SECONDS);
    }

    /**
     * check request id 10s
     *
     * @param requestId request id
     * @return result
     */
    public static boolean processMap(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            return false;
        }
        Long currentTime = System.currentTimeMillis();
        return REQUEST_MAP.compute(requestId, (k, v) -> {
            if (v == null) {
                return currentTime;
            }
            if (currentTime - v >= TIME_WINDOW) {
                return currentTime;
            }
            return v;
        }).equals(currentTime);
    }

    /**
     * check request id 10s
     *
     * @param requestId request id
     * @return result
     */
    public static boolean processCache(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            return false;
        }
        Boolean result = REQUEST_CACHE.asMap().putIfAbsent(requestId, Boolean.TRUE);
        return result == null;
    }
}
