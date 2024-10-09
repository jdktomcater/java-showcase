package com.jdktomcat.showcase.common.disruptor;

import com.jdktomcat.showcase.common.disruptor.event.LongEvent;
import com.jdktomcat.showcase.common.disruptor.event.LongEventFactory;
import com.jdktomcat.showcase.common.disruptor.event.LongEventHandler;
import com.jdktomcat.showcase.common.disruptor.event.LongEventProducerWithTranslator;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class DisruptorAppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DisruptorAppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DisruptorAppTest.class);
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
        ThreadFactory executor = Executors.defaultThreadFactory();
        int bufferSize = 1024;
        Disruptor<LongEvent> disruptor = new Disruptor<>(new LongEventFactory(), bufferSize, executor, ProducerType.SINGLE, new BlockingWaitStrategy());
        disruptor.handleEventsWith(new LongEventHandler());
        disruptor.start();
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        LongEventProducerWithTranslator producerWithTranslator = new LongEventProducerWithTranslator(ringBuffer);
        Thread thread = new Thread(() -> {
            for (long i = 0; i < 10; i++) {
                producerWithTranslator.onData(i);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
        TimeUnit.SECONDS.sleep(10);
    }
}

