package com.jdktomcat.showcase.legacy.system.optimize.parallel.service;

import com.jdktomcat.showcase.legacy.system.optimize.parallel.component.ParallelComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
@Service
public class ParallelService {

    private final ParallelComponent parallelComponent;

    private final BusinessService businessService;

    public ParallelService(ParallelComponent parallelComponent, BusinessService businessService) {
        this.parallelComponent = parallelComponent;
        this.businessService = businessService;
    }

    /**
     * 串行处理：按顺序逐个执行任务
     */
    public List<String> serial(List<Long> ids) {
        List<Long> targetIds = ids != null && !ids.isEmpty() ? ids : defaultIds();
        return parallelComponent.executeSerial(targetIds, businessService::doBusiness);
    }

    /**
     * 并行处理：使用通用组件并发执行任务
     */
    public List<String> parallel(List<Long> ids) {
        List<Long> targetIds = ids != null && !ids.isEmpty() ? ids : defaultIds();
        return parallelComponent.executeParallel(targetIds, businessService::doBusiness);
    }

    /**
     * 并行处理：使用通用组件并发执行任务
     */
    public List<String> pe(List<Long> ids) {
        List<Long> targetIds = ids != null && !ids.isEmpty() ? ids : defaultIds();
        return parallelComponent.executeParallel(targetIds, businessService::doBusiness, (id,ex)->{
            log.error("doBusiness occur exception:", ex);
            return "result-fail-" + id;
        });
    }

    private List<Long> defaultIds() {
        return LongStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
    }
}
