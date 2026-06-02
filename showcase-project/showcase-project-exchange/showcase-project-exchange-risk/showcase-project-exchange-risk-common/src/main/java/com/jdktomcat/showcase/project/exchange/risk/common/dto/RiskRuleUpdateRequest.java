package com.jdktomcat.showcase.project.exchange.risk.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RiskRuleUpdateRequest {

    @NotBlank
    private String ruleName;

    @NotBlank
    private String ruleType;

    @NotBlank
    private String scope;

    private String targetUserId;
    private String targetSymbol;

    private BigDecimal thresholdValue;
    private String thresholdConfig;

    @NotBlank
    private String action;

    @NotNull
    @Min(1)
    private Integer priority;

    private String description;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;

    @NotBlank
    private String operator;
}
