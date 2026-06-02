package com.jdktomcat.showcase.project.exchange.risk.common.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRuleRefreshMessage {

    private long version;
    private String changeType;
    private String ruleCode;
    private String operator;
    private String occurredAt;
}
