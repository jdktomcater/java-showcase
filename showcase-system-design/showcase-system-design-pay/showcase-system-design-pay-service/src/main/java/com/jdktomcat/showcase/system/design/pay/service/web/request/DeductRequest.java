package com.jdktomcat.showcase.system.design.pay.service.web.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * deduct request
 */
@Data
public class DeductRequest {

    @NotBlank
    private String requestId;

    @NotBlank
    private Long userId;

    @DecimalMin(value = "0.00")
    private BigDecimal amount;
}
