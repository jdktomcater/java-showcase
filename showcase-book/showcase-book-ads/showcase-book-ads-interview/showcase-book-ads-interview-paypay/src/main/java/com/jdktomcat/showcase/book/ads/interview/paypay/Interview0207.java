package com.jdktomcat.showcase.book.ads.interview.paypay;

import java.util.concurrent.ConcurrentHashMap;

public class Interview0207 {

    public static final Long TIME_WINDOW = 10000L;

    public static final ConcurrentHashMap<String, Long> REQUEST_MAP = new ConcurrentHashMap<>();

    /**
     * check request id 10s
     *
     * @param requestId request id
     * @return result
     */
    public static boolean process(String requestId) {
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
}
