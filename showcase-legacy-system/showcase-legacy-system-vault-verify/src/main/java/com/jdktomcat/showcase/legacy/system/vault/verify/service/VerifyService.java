package com.jdktomcat.showcase.legacy.system.vault.verify.service;

import com.jdktomcat.showcase.legacy.system.vault.verify.mapper.HealthMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class VerifyService {

    @Value("${verify.target.ids}")
    private String verifyTargetIds;

    @Autowired
    private HealthMapper healthMapper;

    /**
     * duration
     */
    private static final Long TARGET_DURATION = 60 * 1000L;


    /**
     * verify long transaction
     *
     * @throws InterruptedException exception
     */
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
            }
            TimeUnit.MILLISECONDS.sleep(3000);
        }
    }

    /**
     * verify long transaction
     *
     * @throws InterruptedException exception
     */
    public String expand(long holdMs) throws InterruptedException {
        String user = healthMapper.currentUser();
        TimeUnit.MILLISECONDS.sleep(holdMs);
        return user;
    }

    /**
     * verify long transaction
     *
     * @throws InterruptedException exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void exceptionVerify() throws InterruptedException {
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
            }
            TimeUnit.MILLISECONDS.sleep(3000);
            if (System.currentTimeMillis() - start < TARGET_DURATION / 2) {
                throw new RuntimeException("Exception!");
            }
        }
    }
}
