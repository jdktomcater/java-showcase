package com.jdktomcat.showcase.legacy.system.optimize.parallel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

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
            log.info("doBusiness id:{}", id);
            log.info("doBusiness id:{}", id);
            Random rand = new Random();
            // nextInt(61) 生成 0-60，加 50 后范围是 150-250
            int randomNum = rand.nextInt(101) + 50;
            Thread.sleep(TASK_LATENCY_MS + randomNum);
        } catch (Exception e) {
            log.error("doBusiness occur exception:", e);
            return "result-fail-" + id + e.getMessage();
        }
        return "result-ok-" + id;
    }
}
