package com.jdktomcat.showcase.project.exchange.risk.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RiskRuleStatusRequest {

    @NotBlank
    private String status;

    @NotBlank
    private String operator;
}
