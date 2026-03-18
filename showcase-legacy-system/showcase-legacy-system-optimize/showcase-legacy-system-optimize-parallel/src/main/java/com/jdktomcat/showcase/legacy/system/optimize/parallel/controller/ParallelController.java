package com.jdktomcat.showcase.legacy.system.optimize.parallel.controller;

import com.jdktomcat.showcase.legacy.system.optimize.parallel.service.ParallelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
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
        return parallelService.pe(ids);
    }

    @GetMapping("/pe")
    public List<String> pe(@RequestParam(required = false) Integer count) {
        List<Long> ids = toIds(count);
        log.info("pe");
        return parallelService.pe(ids);
    }

    /**
     * 调试用：返回本次请求的 taskCount 与服务器端耗时，便于与 JMeter 压测数据对照
     */
    @GetMapping("/serial/timed")
    public TimedResponse serialTimed(@RequestParam(required = false) Integer count) {
        List<Long> ids = toIds(count);
        long start = System.currentTimeMillis();
        List<String> result = parallelService.serial(ids);
        long elapsedMs = System.currentTimeMillis() - start;
        return new TimedResponse("serial", ids.size(), elapsedMs, result.size());
    }

    @GetMapping("/parallel/timed")
    public TimedResponse parallelTimed(@RequestParam(required = false) Integer count) {
        List<Long> ids = toIds(count);
        long start = System.currentTimeMillis();
        List<String> result = parallelService.parallel(ids);
        long elapsedMs = System.currentTimeMillis() - start;
        return new TimedResponse("parallel", ids.size(), elapsedMs, result.size());
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

    /** 带耗时的响应，便于与 JMeter 对照 */
    public static class TimedResponse {
        public final String mode;
        public final int taskCount;
        public final long serverMs;
        public final int resultSize;
        /** 理论耗时（串行=taskCount×100ms，并行≈100ms） */
        public final String expectedNote;

        public TimedResponse(String mode, int taskCount, long serverMs, int resultSize) {
            this.mode = mode;
            this.taskCount = taskCount;
            this.serverMs = serverMs;
            this.resultSize = resultSize;
            this.expectedNote = "serial".equals(mode)
                    ? taskCount + "×100ms≈" + (taskCount * 100) + "ms"
                    : "parallel≈100-200ms";
        }
    }
}
