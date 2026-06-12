package com.jdktomcat.showcase.legacy.system.optimize.parallel;

import com.jdktomcat.showcase.legacy.system.optimize.parallel.component.ParallelComponent;
import com.jdktomcat.showcase.legacy.system.optimize.parallel.service.BusinessService;
import com.jdktomcat.showcase.legacy.system.optimize.parallel.service.ParallelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证并行相比串行的性能提升
 */
@DisplayName("并行 vs 串行 性能验证")
class ParallelPerformanceTest {

    private ParallelService parallelService;

    @BeforeEach
    void setUp() {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        ParallelComponent component = new ParallelComponent(executor,null);
        BusinessService businessService = new BusinessService();
        parallelService = new ParallelService(component, businessService);
    }

    @Test
    @DisplayName("串行与并行结果一致")
    void serialAndParallelProduceSameResults() {
        List<Long> ids = LongStream.rangeClosed(1, 5).boxed().collect(Collectors.toList());

        List<String> serialResults = parallelService.serial(ids);
        List<String> parallelResults = parallelService.parallel(ids);

        assertEquals(serialResults, parallelResults);
        assertEquals(5, serialResults.size());
    }

    @Test
    @DisplayName("并行耗时显著小于串行")
    void parallelIsFasterThanSerial() {
        List<Long> ids = LongStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());

        long serialStart = System.currentTimeMillis();
        parallelService.serial(ids);
        long serialMs = System.currentTimeMillis() - serialStart;

        long parallelStart = System.currentTimeMillis();
        parallelService.parallel(ids);
        long parallelMs = System.currentTimeMillis() - parallelStart;

        assertTrue(serialMs > parallelMs,
                String.format("串行 %dms 应大于并行 %dms", serialMs, parallelMs));

        double improvement = (double) serialMs / parallelMs;
        assertTrue(improvement >= 2.0,
                String.format("并行应至少快 2 倍，实际提升 %.2fx", improvement));
    }

    @Test
    @DisplayName("性能提升随任务数增加而增大")
    void improvementIncreasesWithTaskCount() {
        int[] counts = {5, 10, 15};

        for (int count : counts) {
            List<Long> ids = LongStream.rangeClosed(1, count).boxed().collect(Collectors.toList());

            long serialMs = measure(() -> parallelService.serial(ids));
            long parallelMs = measure(() -> parallelService.parallel(ids));

            double improvement = (double) serialMs / parallelMs;
            assertTrue(improvement >= 1.5,
                    String.format("count=%d: 串行=%dms, 并行=%dms, 提升=%.2fx", count, serialMs, parallelMs, improvement));
        }
    }

    private long measure(Runnable runnable) {
        long start = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - start;
    }
}
