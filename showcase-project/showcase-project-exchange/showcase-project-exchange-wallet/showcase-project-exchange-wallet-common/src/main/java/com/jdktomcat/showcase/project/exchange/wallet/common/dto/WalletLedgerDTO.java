package com.jdktomcat.showcase.project.exchange.wallet.common.dto;

import com.jdktomcat.showcase.project.exchange.wallet.common.enums.LedgerEntryType;
import com.jdktomcat.showcase.project.exchange.wallet.common.enums.WalletOperationType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletLedgerDTO {

    private Long id;
    private Long accountId;
    private Long userId;
    private String currency;
    private String bizRequestId;
    private WalletOperationType operationType;
    private LedgerEntryType entryType;
    private BigDecimal amount;
    private BigDecimal totalBefore;
    private BigDecimal totalAfter;
    private BigDecimal availableBefore;
    private BigDecimal availableAfter;
    private BigDecimal frozenBefore;
    private BigDecimal frozenAfter;
    private LocalDateTime createdAt;
}
