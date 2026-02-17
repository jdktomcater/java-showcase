package com.jdktomcat.showcase.common.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class DisruptorApplicationTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DisruptorApplicationTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DisruptorApplicationTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    /**
     * 测试单一生产者单一消费者
     */
    public void testSimpleProducerDisruptorWithMethodReference() throws InterruptedException {

    }
}

