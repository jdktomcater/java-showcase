package com.jdktomcat.showcase.project.exchange.wallet.common.dto;

import com.jdktomcat.showcase.project.exchange.wallet.common.enums.OperationStatus;
import com.jdktomcat.showcase.project.exchange.wallet.common.enums.WalletOperationType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletOperationResultDTO {

    private Long operationId;
    private String bizRequestId;
    private WalletOperationType operationType;
    private OperationStatus status;
    private Long userId;
    private String currency;
    private BigDecimal amount;
    private WalletBalanceDTO balanceAfter;
    private boolean idempotentReplay;
    private LocalDateTime createdAt;
}
