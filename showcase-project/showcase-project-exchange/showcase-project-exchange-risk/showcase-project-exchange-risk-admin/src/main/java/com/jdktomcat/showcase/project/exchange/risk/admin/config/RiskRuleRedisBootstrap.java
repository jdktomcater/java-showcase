package com.jdktomcat.showcase.project.exchange.risk.admin.config;

import com.jdktomcat.showcase.project.exchange.risk.admin.redis.RiskRuleRedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "exchange.risk.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RiskRuleRedisBootstrap implements ApplicationRunner {

    private final RiskRuleRedisPublisher redisSyncService;

    @Override
    public void run(ApplicationArguments args) {
        redisSyncService.syncAll("BOOTSTRAP", null, "system");
    }
}
