package com.jdktomcat.showcase.project.exchange.risk.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RiskRuleTypeInfo {

    private String code;
    private String label;
    private String description;
}
