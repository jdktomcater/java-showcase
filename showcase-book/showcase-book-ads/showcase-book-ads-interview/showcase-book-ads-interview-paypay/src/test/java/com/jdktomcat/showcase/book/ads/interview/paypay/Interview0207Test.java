package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class Interview0207Test {

    @Test
    public void processMap() throws InterruptedException {
        String requestId = UUID.randomUUID().toString();
        boolean first = Interview0207.processMap(requestId);
        assertTrue(first);
        TimeUnit.SECONDS.sleep(1);
        boolean second = Interview0207.processMap(requestId);
        assertFalse(second);
        TimeUnit.SECONDS.sleep(9);
        boolean third = Interview0207.processMap(requestId);
        assertTrue(third);
    }

    @Test
    public void processCache() throws InterruptedException {
        String requestId = UUID.randomUUID().toString();
        boolean first = Interview0207.processCache(requestId);
        assertTrue(first);
        TimeUnit.SECONDS.sleep(1);
        boolean second = Interview0207.processCache(requestId);
        assertFalse(second);
        TimeUnit.SECONDS.sleep(9);
        boolean third = Interview0207.processCache(requestId);
        assertTrue(third);
    }
}