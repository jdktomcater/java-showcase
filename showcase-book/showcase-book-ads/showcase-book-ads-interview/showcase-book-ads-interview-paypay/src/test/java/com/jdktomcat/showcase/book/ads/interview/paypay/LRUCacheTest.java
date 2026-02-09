package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LRUCacheTest {

    @Test
    public void lRUCacheTest() {
        LRUCache lruCache = new LRUCache(2);
        int value = lruCache.get(1);
        assertEquals(-1, value);
        lruCache.put(1, 1);
        value = lruCache.get(1);
        assertEquals(1, value);
        lruCache.put(1, 2);
        value = lruCache.get(1);
        assertEquals(2, value);
        lruCache.put(2, 3);
        value = lruCache.get(2);
        assertEquals(3, value);
        lruCache.put(3, 4);
        value = lruCache.get(3);
        assertEquals(4, value);
        value = lruCache.get(1);
        assertEquals(-1, value);
    }
}