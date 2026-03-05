package com.jdktomcat.showcase.legacy.system.optimize.parallel.controller;

import com.jdktomcat.showcase.legacy.system.optimize.parallel.service.ParallelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@RestController
public class ParallelController {

    private final ParallelService parallelService;

    public ParallelController(ParallelService parallelService) {
        this.parallelService = parallelService;
    }

    @GetMapping("/serial")
    public List<String> serial(@RequestParam(required = false) Integer count) {
        List<Long> ids = toIds(count);
        return parallelService.serial(ids);
    }

    @GetMapping("/parallel")
    public List<String> parallel(@RequestParam(required = false) Integer count) {
        List<Long> ids = toIds(count);
        return parallelService.parallel(ids);
    }

    /**
     * 性能对比基准：串行 vs 并行，返回耗时与提升倍数
     *
     * @param count 任务数量，默认 10
     * @return 串行耗时、并行耗时、提升倍数
     */
    @GetMapping("/benchmark")
    public BenchmarkResult benchmark(@RequestParam(defaultValue = "10") int count) {
        List<Long> ids = LongStream.rangeClosed(1, count).boxed().collect(Collectors.toList());

        long serialStart = System.currentTimeMillis();
        parallelService.serial(ids);
        long serialMs = System.currentTimeMillis() - serialStart;

        long parallelStart = System.currentTimeMillis();
        parallelService.parallel(ids);
        long parallelMs = System.currentTimeMillis() - parallelStart;

        double improvement = serialMs > 0 ? (double) serialMs / parallelMs : 0;

        return new BenchmarkResult(count, serialMs, parallelMs, improvement);
    }

    private List<Long> toIds(Integer count) {
        int n = (count != null && count > 0) ? count : 10;
        return LongStream.rangeClosed(1, n).boxed().collect(Collectors.toList());
    }

    /** 基准测试结果 */
    public static class BenchmarkResult {
        public final int taskCount;
        public final long serialMs;
        public final long parallelMs;
        public final double improvement;

        public BenchmarkResult(int taskCount, long serialMs, long parallelMs, double improvement) {
            this.taskCount = taskCount;
            this.serialMs = serialMs;
            this.parallelMs = parallelMs;
            this.improvement = improvement;
        }
    }
}
