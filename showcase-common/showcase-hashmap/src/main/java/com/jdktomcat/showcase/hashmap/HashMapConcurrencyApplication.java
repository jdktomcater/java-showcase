package com.jdktomcat.showcase.hashmap;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Demonstrates that HashMap is unsafe when multiple threads write to it concurrently.
 */
public class HashMapConcurrencyApplication {

    private static final int DEFAULT_ROUNDS = 50;
    private static final int DEFAULT_THREADS = 16;
    private static final int DEFAULT_ENTRIES_PER_THREAD = 2_000;
    private static final Duration ROUND_TIMEOUT = Duration.ofSeconds(10);

    public static void main(String[] args) throws InterruptedException {
        Config config = Config.from(args);

        System.out.printf("并发写入验证: rounds=%d, threads=%d, entriesPerThread=%d, expectedSize=%d%n",
                config.rounds(), config.threads(), config.entriesPerThread(), config.expectedSize());
        System.out.println();

        System.out.println("1. HashMap 多线程并发 put");
        TrialResult hashMapResult = runUntilProblem("HashMap", HashMap::new, config);
        printResult(hashMapResult, true);
        System.out.println();

        System.out.println("2. ConcurrentHashMap 对照");
        TrialResult concurrentHashMapResult = runFixedRounds("ConcurrentHashMap", ConcurrentHashMap::new, config.referenceRounds());
        printResult(concurrentHashMapResult, false);
        System.out.println();

        System.out.println("3. Collections.synchronizedMap 对照");
        TrialResult synchronizedMapResult = runFixedRounds("synchronizedMap",
                () -> Collections.synchronizedMap(new HashMap<>()), config.referenceRounds());
        printResult(synchronizedMapResult, false);
    }

    private static TrialResult runUntilProblem(String name, Supplier<Map<DemoKey, Integer>> mapSupplier, Config config)
            throws InterruptedException {
        TrialResult lastResult = null;
        for (int round = 1; round <= config.rounds(); round++) {
            TrialResult result = runOneRound(name, mapSupplier, config.threads(), config.entriesPerThread(), round);
            lastResult = result;
            if (result.hasProblem()) {
                return result;
            }
        }
        return lastResult;
    }

    private static TrialResult runFixedRounds(String name, Supplier<Map<DemoKey, Integer>> mapSupplier, Config config)
            throws InterruptedException {
        TrialResult lastResult = null;
        for (int round = 1; round <= config.rounds(); round++) {
            TrialResult result = runOneRound(name, mapSupplier, config.threads(), config.entriesPerThread(), round);
            lastResult = result;
            if (result.hasProblem()) {
                return result;
            }
        }
        return lastResult;
    }

    private static TrialResult runOneRound(String name, Supplier<Map<DemoKey, Integer>> mapSupplier,
                                           int threads, int entriesPerThread, int round)
            throws InterruptedException {
        Map<DemoKey, Integer> map = mapSupplier.get();
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(threads);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        ExecutorService executor = Executors.newFixedThreadPool(threads, daemonThreadFactory(name + "-writer-"));

        for (int threadIndex = 0; threadIndex < threads; threadIndex++) {
            int writerId = threadIndex;
            executor.execute(() -> {
                ready.countDown();
                try {
                    start.await();
                    int base = writerId * entriesPerThread;
                    for (int offset = 0; offset < entriesPerThread; offset++) {
                        int value = base + offset;
                        map.put(new DemoKey(value), value);
                    }
                } catch (Throwable throwable) {
                    failure.compareAndSet(null, throwable);
                } finally {
                    finished.countDown();
                }
            });
        }

        ready.await();
        long startedAt = System.nanoTime();
        start.countDown();
        boolean completed = finished.await(ROUND_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
        executor.shutdownNow();

        int expectedSize = threads * entriesPerThread;
        Throwable throwable = failure.get();
        if (!completed) {
            return TrialResult.timeout(name, round, expectedSize, map.size(), elapsedMillis);
        }
        if (throwable != null) {
            return TrialResult.failure(name, round, expectedSize, map.size(), elapsedMillis, throwable);
        }
        return TrialResult.completed(name, round, expectedSize, map.size(), elapsedMillis);
    }

    private static ThreadFactory daemonThreadFactory(String prefix) {
        AtomicInteger sequence = new AtomicInteger();
        return task -> {
            Thread thread = new Thread(task, prefix + sequence.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }

    private static void printResult(TrialResult result, boolean warnWhenNoProblem) {
        if (result == null) {
            System.out.println("未执行验证。");
            return;
        }

        System.out.printf("%s round=%d expected=%d actual=%d cost=%dms%n",
                result.name(), result.round(), result.expectedSize(), result.actualSize(), result.elapsedMillis());

        if (result.timeout()) {
            System.out.println("结果: 线程未在超时时间内完成，HashMap 内部结构可能已经被并发写破坏。");
            return;
        }
        if (result.throwable() != null) {
            System.out.printf("结果: 写入过程中出现异常: %s: %s%n",
                    result.throwable().getClass().getName(), result.throwable().getMessage());
            return;
        }
        if (result.actualSize() != result.expectedSize()) {
            System.out.printf("结果: size 不一致，丢失了 %d 个元素。%n",
                    result.expectedSize() - result.actualSize());
            return;
        }

        if (warnWhenNoProblem) {
            System.out.println("结果: 本轮未观察到异常。HashMap 的并发问题是竞态，未复现不代表线程安全。");
            return;
        }
        System.out.println("结果: 对照组符合预期。");
    }

    private record DemoKey(int value) implements Comparable<DemoKey> {

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public int compareTo(DemoKey other) {
            return Integer.compare(value, other.value);
        }
    }

    private record Config(int rounds, int threads, int entriesPerThread) {

        private static Config from(String[] args) {
            int rounds = parseArg(args, 0, DEFAULT_ROUNDS);
            int threads = parseArg(args, 1, DEFAULT_THREADS);
            int entriesPerThread = parseArg(args, 2, DEFAULT_ENTRIES_PER_THREAD);
            return new Config(rounds, threads, entriesPerThread);
        }

        private static int parseArg(String[] args, int index, int defaultValue) {
            if (args.length <= index) {
                return defaultValue;
            }
            return Integer.parseInt(args[index]);
        }

        private int expectedSize() {
            return threads * entriesPerThread;
        }

        private Config referenceRounds() {
            return new Config(Math.min(rounds, 3), threads, entriesPerThread);
        }
    }

    private record TrialResult(String name, int round, int expectedSize, int actualSize, long elapsedMillis,
                               boolean timeout, Throwable throwable) {

        private static TrialResult completed(String name, int round, int expectedSize, int actualSize,
                                             long elapsedMillis) {
            return new TrialResult(name, round, expectedSize, actualSize, elapsedMillis, false, null);
        }

        private static TrialResult timeout(String name, int round, int expectedSize, int actualSize,
                                           long elapsedMillis) {
            return new TrialResult(name, round, expectedSize, actualSize, elapsedMillis, true, null);
        }

        private static TrialResult failure(String name, int round, int expectedSize, int actualSize,
                                           long elapsedMillis, Throwable throwable) {
            return new TrialResult(name, round, expectedSize, actualSize, elapsedMillis, false, throwable);
        }

        private boolean hasProblem() {
            return timeout || throwable != null || actualSize != expectedSize;
        }
    }
}
