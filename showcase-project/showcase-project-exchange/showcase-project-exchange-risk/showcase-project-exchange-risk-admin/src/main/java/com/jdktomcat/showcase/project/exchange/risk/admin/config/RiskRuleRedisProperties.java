package com.jdktomcat.showcase.project.exchange.risk.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "exchange.risk.redis")
public class RiskRuleRedisProperties {

    /**
     * When false, skip Redis writes and pub/sub (useful for local dev without Redis).
     */
    private boolean enabled = true;
}
