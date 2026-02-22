package com.jdktomcat.showcase.legacy.system.vault.verify.service;

import com.jdktomcat.showcase.legacy.system.vault.verify.mapper.HealthMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class VerifyService {

    @Value("${verify.target.ids}")
    private String verifyTargetIds;

    @Autowired
    private HealthMapper healthMapper;

    /**
     * 目标时常
     */
    private static final Long TARGET_DURATION = 60 * 1000L;


    @Transactional
    public void tranVerify() throws InterruptedException {
        String[] targetIds = verifyTargetIds.split(",");
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < TARGET_DURATION) {
            for (String targetId : targetIds) {
                Long id = Long.valueOf(targetId);
                String ruleName = "rule_name_" + System.currentTimeMillis();
                int ping = healthMapper.ping();
                String user = healthMapper.currentUser();
                int update = healthMapper.update(id, ruleName);
                log.info("user:{} ping:{} update:{}", user, ping, update);
                TimeUnit.MILLISECONDS.sleep(300);
            }
        }
    }
}
