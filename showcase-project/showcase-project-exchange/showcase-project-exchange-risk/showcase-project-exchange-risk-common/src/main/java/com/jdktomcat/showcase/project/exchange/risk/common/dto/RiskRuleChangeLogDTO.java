package com.jdktomcat.showcase.project.exchange.risk.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRuleChangeLogDTO {

    private Long id;
    private Long ruleId;
    private String ruleCode;
    private String changeType;
    private String beforeSnapshot;
    private String afterSnapshot;
    private String operator;
    private LocalDateTime createdAt;
}
