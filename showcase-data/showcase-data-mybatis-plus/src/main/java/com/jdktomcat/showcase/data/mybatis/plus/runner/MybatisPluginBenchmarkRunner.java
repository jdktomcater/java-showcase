package com.jdktomcat.showcase.data.mybatis.plus.runner;

import com.jdktomcat.showcase.data.mybatis.plus.config.BenchmarkProperties;
import com.jdktomcat.showcase.data.mybatis.plus.entry.ChannelAccountRecord;
import com.jdktomcat.showcase.data.mybatis.plus.mapper.ChannelAccountRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于 CommandLineRunner 的 MyBatis 插件性能验证入口：
 * - 通过 retry.interceptor.enabled=true/false 做 A/B 对照
 * - 输出 QPS / P50 / P95 / P99 / error rate
 */
@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
public class MybatisPluginBenchmarkRunner implements CommandLineRunner {

    private final BenchmarkProperties props;
    private final ChannelAccountRecordMapper channelAccountRecordMapper;

    @Override
    public void run(String... args) throws Exception {
        if (!props.isEnabled()) {
            return;
        }

        log.info("=== MyBatis plugin benchmark start ===");
        log.info("threads={} warmupOps={} ops={} setIdExplicitly={} conflictRate={} maxSamples={}",
                props.getThreads(), props.getWarmupOps(), props.getOps(),
                props.isSetIdExplicitly(), props.getConflictRate(), props.getMaxSamples());

        if (props.getThreads() <= 0) {
            throw new IllegalArgumentException("benchmark.threads must be > 0");
        }
        if (props.getWarmupOps() < 0 || props.getOps() <= 0) {
            throw new IllegalArgumentException("benchmark.warmupOps must be >= 0 and benchmark.ops must be > 0");
        }
        if (props.getConflictRate() < 0.0d || props.getConflictRate() > 1.0d) {
            throw new IllegalArgumentException("benchmark.conflictRate must be in [0,1]");
        }

        // 预热（不统计）
        if (props.getWarmupOps() > 0) {
            runOnce(props.getWarmupOps(), false);
            log.info("warmup done");
        }

        // 正式统计
        BenchmarkResult result = runOnce(props.getOps(), true);
        log.info("=== MyBatis plugin benchmark result ===");
        log.info("durationMs={} totalOps={} success={} fail={} errorRate={} qps={}",
                result.durationMs,
                props.getOps(),
                result.success,
                result.fail,
                String.format("%.4f", (result.fail * 1.0d) / Math.max(1, props.getOps())),
                String.format("%.2f", (result.success * 1000.0d) / Math.max(1, result.durationMs)));

        if (result.samples != null && result.samples.length > 0) {
            Arrays.sort(result.samples);
            log.info("latencyMs p50={}ms p95={}ms p99={}ms (samples={})",
                    formatMs(percentile(result.samples, 0.50)),
                    formatMs(percentile(result.samples, 0.95)),
                    formatMs(percentile(result.samples, 0.99)),
                    result.samples.length);
        } else {
            log.info("latency samples empty (maybe ops too small?)");
        }
        log.info("=== MyBatis plugin benchmark end ===");
    }

    private BenchmarkResult runOnce(long totalOps, boolean recordLatency) throws InterruptedException {
        int threads = props.getThreads();
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        long maxSamples = Math.max(1, props.getMaxSamples());
        long sampleEvery = recordLatency ? Math.max(1, totalOps / maxSamples) : Long.MAX_VALUE;
        int expectedSamples = recordLatency ? (int) Math.min(maxSamples, (totalOps + sampleEvery - 1) / sampleEvery) : 0;
        SampleCollector collector = recordLatency ? new SampleCollector(expectedSamples) : null;

        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(threads);
        AtomicLong success = new AtomicLong(0);
        AtomicLong fail = new AtomicLong(0);
        AtomicLong seq = new AtomicLong(0);

        long perThread = totalOps / threads;
        long remainder = totalOps % threads;

        for (int t = 0; t < threads; t++) {
            long opsForThisThread = perThread + (t < remainder ? 1 : 0);
            pool.submit(() -> {
                Random rnd = new Random();
                try {
                    startGate.await();
                    for (long i = 0; i < opsForThisThread; i++) {
                        long opIndex = seq.getAndIncrement();

                        long begin = System.nanoTime();
                        try {
                            ChannelAccountRecord entity = new ChannelAccountRecord();
                            // 冲突场景：按比例使用固定主键值，制造主键冲突
                            boolean makeConflict = props.getConflictRate() > 0.0d && rnd.nextDouble() < props.getConflictRate();
                            if (makeConflict) {
                                entity.setOrderIndex(props.getFixedOrderIndex());
                            } else if (props.isSetIdExplicitly()) {
                                entity.setOrderIndex(props.getBaseOrderIndex() + opIndex);
                            } else {
                                // 置空，依赖 mybatis-plus ASSIGN_ID
                                entity.setOrderIndex(null);
                            }
                            entity.setPayChannelID(10L);
                            entity.setOrderID("bench-" + opIndex);
                            entity.setCreateTime(new Date());
                            channelAccountRecordMapper.insert(entity);
                            success.incrementAndGet();
                        } catch (Exception e) {
                            fail.incrementAndGet();
                        } finally {
                            long end = System.nanoTime();
                            if (collector != null && (opIndex % sampleEvery == 0)) {
                                collector.add(end - begin);
                            }
                        }
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneGate.countDown();
                }
            });
        }

        long startNs = System.nanoTime();
        startGate.countDown();
        doneGate.await();
        long endNs = System.nanoTime();

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        long durationMs = TimeUnit.NANOSECONDS.toMillis(endNs - startNs);
        long[] samples = collector == null ? null : collector.snapshot();
        return new BenchmarkResult(durationMs, success.get(), fail.get(), samples);
    }

    private static double percentile(long[] sortedNs, double p) {
        if (sortedNs.length == 0) return 0;
        int idx = (int) Math.ceil(p * sortedNs.length) - 1;
        idx = Math.max(0, Math.min(sortedNs.length - 1, idx));
        return sortedNs[idx] / 1_000_000.0d;
    }

    private static String formatMs(double ms) {
        return String.format("%.3f", ms);
    }

    private record BenchmarkResult(long durationMs, long success, long fail, long[] samples) {}

    private static final class SampleCollector {
        private final long[] buf;
        private int pos = 0;

        private SampleCollector(int capacity) {
            this.buf = new long[Math.max(1, capacity)];
        }

        private synchronized void add(long ns) {
            if (pos >= buf.length) return;
            buf[pos++] = ns;
        }

        private synchronized long[] snapshot() {
            return Arrays.copyOf(buf, pos);
        }
    }
}


