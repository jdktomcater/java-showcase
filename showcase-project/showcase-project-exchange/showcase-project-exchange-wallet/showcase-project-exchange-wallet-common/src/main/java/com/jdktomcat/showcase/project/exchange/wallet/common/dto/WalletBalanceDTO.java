package com.jdktomcat.showcase.project.exchange.wallet.common.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletBalanceDTO {

    private Long accountId;
    private Long userId;
    private String currency;
    private BigDecimal total;
    private BigDecimal available;
    private BigDecimal frozen;
    private Long version;
    private LocalDateTime updatedAt;
}
