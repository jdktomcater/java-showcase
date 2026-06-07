package com.jdktomcat.showcase.project.exchange.wallet.common.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String currency;

    @NotBlank
    private String bizRequestId;
}
