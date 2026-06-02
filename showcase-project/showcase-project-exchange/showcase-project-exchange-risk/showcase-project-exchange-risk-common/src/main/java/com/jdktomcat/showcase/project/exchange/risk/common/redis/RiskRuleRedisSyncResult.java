package com.jdktomcat.showcase.project.exchange.risk.common.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRuleRedisSyncResult {

    private long version;
    private int ruleCount;
    private String changeType;
    private boolean published;
    private String message;
}
