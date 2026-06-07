package com.jdktomcat.showcase.project.exchange.wallet.service.domain;

import com.jdktomcat.showcase.project.exchange.wallet.common.enums.LedgerEntryType;
import com.jdktomcat.showcase.project.exchange.wallet.common.enums.WalletOperationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Append-only ledger for every balance mutation with before/after snapshots.
 */
@Getter
@Setter
@Entity
@Table(name = "wallet_ledger", indexes = {
        @Index(name = "idx_ledger_account_time", columnList = "account_id, created_at"),
        @Index(name = "idx_ledger_biz_request", columnList = "biz_request_id")
})
public class WalletLedgerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "currency", nullable = false, length = 16)
    private String currency;

    @Column(name = "biz_request_id", nullable = false, length = 64)
    private String bizRequestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 32)
    private WalletOperationType operationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 32)
    private LedgerEntryType entryType;

    @Column(name = "amount", nullable = false, precision = 30, scale = 8)
    private BigDecimal amount;

    @Column(name = "total_before", nullable = false, precision = 30, scale = 8)
    private BigDecimal totalBefore;

    @Column(name = "total_after", nullable = false, precision = 30, scale = 8)
    private BigDecimal totalAfter;

    @Column(name = "available_before", nullable = false, precision = 30, scale = 8)
    private BigDecimal availableBefore;

    @Column(name = "available_after", nullable = false, precision = 30, scale = 8)
    private BigDecimal availableAfter;

    @Column(name = "frozen_before", nullable = false, precision = 30, scale = 8)
    private BigDecimal frozenBefore;

    @Column(name = "frozen_after", nullable = false, precision = 30, scale = 8)
    private BigDecimal frozenAfter;

    @Column(name = "remark", length = 256)
    private String remark;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
