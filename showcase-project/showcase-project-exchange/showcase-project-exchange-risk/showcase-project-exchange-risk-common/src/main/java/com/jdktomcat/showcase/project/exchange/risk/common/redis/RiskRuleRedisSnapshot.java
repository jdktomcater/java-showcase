package com.jdktomcat.showcase.project.exchange.risk.common.redis;

import com.jdktomcat.showcase.project.exchange.risk.common.dto.RiskRuleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRuleRedisSnapshot {

    private long version;
    private List<RiskRuleDTO> rules;
}
