package com.jdktomcat.showcase.legacy.system.vault.job;

import com.jdktomcat.showcase.legacy.system.vault.mapper.HealthMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobHandler(value = "vaultCheck")
public class VaultCheckJob extends IJobHandler {

    /**
     * health check mapper
     */
    @Autowired
    private HealthMapper healthMapper;

    @Override
    public ReturnT<String> execute(String param) {
        long t1 = System.currentTimeMillis();
        log.info("定时任务：验证定时任务vault开始");
        int result = healthMapper.ping();
        log.info("定时任务：验证定时任务vault结束,耗时:{} 结果：{}", System.currentTimeMillis() - t1, result);
        return ReturnT.SUCCESS;
    }
}
