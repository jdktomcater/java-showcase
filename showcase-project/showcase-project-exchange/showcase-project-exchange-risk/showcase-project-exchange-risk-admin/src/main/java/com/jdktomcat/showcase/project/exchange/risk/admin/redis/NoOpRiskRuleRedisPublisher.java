package com.jdktomcat.showcase.project.exchange.risk.admin.redis;

import com.jdktomcat.showcase.project.exchange.risk.common.redis.RiskRuleRedisSnapshot;
import com.jdktomcat.showcase.project.exchange.risk.common.redis.RiskRuleRedisSyncResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@ConditionalOnProperty(name = "exchange.risk.redis.enabled", havingValue = "false")
public class NoOpRiskRuleRedisPublisher implements RiskRuleRedisPublisher {

    @Override
    public RiskRuleRedisSyncResult syncAll(String changeType, String ruleCode, String operator) {
        return RiskRuleRedisSyncResult.builder()
                .changeType(changeType)
                .published(false)
                .message("Redis sync disabled by configuration")
                .build();
    }

    @Override
    public Optional<RiskRuleRedisSnapshot> loadSnapshotFromRedis() {
        return Optional.empty();
    }

    @Override
    public RiskRuleRedisSyncResult getCurrentVersion() {
        return RiskRuleRedisSyncResult.builder()
                .published(false)
                .message("Redis sync disabled by configuration")
                .build();
    }
}
