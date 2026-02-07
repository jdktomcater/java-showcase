package com.jdktomcat.showcase.book.ads.interview.paypay;

import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class Interview0207Test {

    @Test
    public void process() throws InterruptedException {
        String requestId = UUID.randomUUID().toString();
        boolean first = Interview0207.process(requestId);
        assertTrue(first);
        TimeUnit.SECONDS.sleep(1);
        boolean second = Interview0207.process(requestId);
        assertFalse(second);
        TimeUnit.SECONDS.sleep(9);
        boolean third = Interview0207.process(requestId);
        assertTrue(third);
    }
}