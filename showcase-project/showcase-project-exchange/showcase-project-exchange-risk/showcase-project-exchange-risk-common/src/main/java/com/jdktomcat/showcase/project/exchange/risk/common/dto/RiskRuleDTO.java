package com.jdktomcat.showcase.project.exchange.risk.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRuleDTO {

    private Long id;
    private String ruleCode;
    private String ruleName;
    private String ruleType;
    private String ruleTypeLabel;
    private String scope;
    private String scopeLabel;
    private String targetUserId;
    private String targetSymbol;
    private BigDecimal thresholdValue;
    private String thresholdConfig;
    private String action;
    private String actionLabel;
    private Integer priority;
    private String status;
    private String statusLabel;
    private String description;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
