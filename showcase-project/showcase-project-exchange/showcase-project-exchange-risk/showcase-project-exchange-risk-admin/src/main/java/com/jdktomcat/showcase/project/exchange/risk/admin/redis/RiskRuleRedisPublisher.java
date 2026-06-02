package com.jdktomcat.showcase.project.exchange.risk.admin.redis;

import com.jdktomcat.showcase.project.exchange.risk.common.redis.RiskRuleRedisSnapshot;
import com.jdktomcat.showcase.project.exchange.risk.common.redis.RiskRuleRedisSyncResult;

import java.util.Optional;

public interface RiskRuleRedisPublisher {

    RiskRuleRedisSyncResult syncAll(String changeType, String ruleCode, String operator);

    Optional<RiskRuleRedisSnapshot> loadSnapshotFromRedis();

    RiskRuleRedisSyncResult getCurrentVersion();
}
