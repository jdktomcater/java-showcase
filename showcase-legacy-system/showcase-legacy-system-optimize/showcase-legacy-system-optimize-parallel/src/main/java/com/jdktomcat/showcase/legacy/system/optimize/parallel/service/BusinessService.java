package com.jdktomcat.showcase.legacy.system.optimize.parallel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BusinessService {

    /**
     * 模拟单次任务耗时（毫秒），如 RPC/DB 调用
     */
    private static final long TASK_LATENCY_MS = 100;

    /**
     * business task
     *
     * @param id target id
     * @return result
     */
    public String doBusiness(Long id) {
        try {
            log.info("doBusiness id:{}", id);
            Thread.sleep(TASK_LATENCY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return "result-" + id;
    }
}
