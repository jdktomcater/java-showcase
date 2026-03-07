package com.jdktomcat.showcase.legacy.system.optimize.parallel.service;

import com.jdktomcat.showcase.legacy.system.optimize.parallel.component.ParallelComponent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class ParallelService {

    /** 模拟单次任务耗时（毫秒），如 RPC/DB 调用 */
    private static final long TASK_LATENCY_MS = 100;

    private final ParallelComponent parallelComponent;

    public ParallelService(ParallelComponent parallelComponent) {
        this.parallelComponent = parallelComponent;
    }

    /**
     * 模拟耗时任务（如远程调用、数据库查询）
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
     */
    public List<String> serial(List<Long> ids) {
        List<Long> targetIds = ids != null && !ids.isEmpty() ? ids : defaultIds();
        return parallelComponent.executeSerial(targetIds, this::simulateTask);
    }

    /**
     * 并行处理：使用通用组件并发执行任务
     */
    public List<String> parallel(List<Long> ids) {
        List<Long> targetIds = ids != null && !ids.isEmpty() ? ids : defaultIds();
        return parallelComponent.executeParallel(targetIds, this::simulateTask);
    }

    private List<Long> defaultIds() {
        return LongStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
    }
}
