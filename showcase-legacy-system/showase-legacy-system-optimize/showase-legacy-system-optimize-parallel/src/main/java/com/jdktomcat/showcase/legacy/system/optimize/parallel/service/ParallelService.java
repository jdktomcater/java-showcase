package com.jdktomcat.showcase.legacy.system.optimize.parallel.service;

import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class ParallelService {

    /** 模拟单次任务耗时（毫秒），如 RPC/DB 调用 */
    private static final long TASK_LATENCY_MS = 100;

    /**
     * 专用线程池：避免与 ForkJoinPool.commonPool 竞争。
     * 压测时多请求并发会各自提交 N 个任务，commonPool 易饱和导致并行反而更慢。
     */
    private final ExecutorService parallelExecutor = Executors.newFixedThreadPool(50);

    /**
     * 模拟耗时任务（如远程调用、数据库查询）
     *
     * @param id 任务 ID
     * @return 处理结果
     */
    private String simulateTask(long id) {
        try {
            Thread.sleep(TASK_LATENCY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return "result-" + id;
    }

    /**
     * 串行处理：按顺序逐个执行任务
     *
     * @param ids target id list
     * @return result
     */
    public List<String> serial(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            ids = defaultIds();
        }
        List<String> results = new ArrayList<>(ids.size());
        for (Long id : ids) {
            results.add(simulateTask(id));
        }
        return results;
    }

    /**
     * 并行处理：使用 CompletableFuture 并发执行任务
     *
     * @param ids target id list
     * @return result（保持与 ids 顺序一致）
     */
    public List<String> parallel(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            ids = defaultIds();
        }
        List<CompletableFuture<String>> futures = ids.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> simulateTask(id), parallelExecutor))
                .collect(Collectors.toList());
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private List<Long> defaultIds() {
        return LongStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
    }

    @PreDestroy
    void shutdown() {
        parallelExecutor.shutdown();
    }
}
