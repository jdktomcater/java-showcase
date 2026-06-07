package com.jdktomcat.showcase.project.exchange.wallet.common.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletAmountRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String currency;

    @NotNull
    @DecimalMin(value = "0.00000001", message = "amount must be positive")
    private BigDecimal amount;

    @NotBlank
    private String bizRequestId;

    private String remark;
}
