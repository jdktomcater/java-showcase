package com.jdktomcat.showcase.project.exchange.wallet.service.domain;

import com.jdktomcat.showcase.project.exchange.wallet.common.enums.OperationStatus;
import com.jdktomcat.showcase.project.exchange.wallet.common.enums.WalletOperationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Idempotency record: (bizRequestId, operationType) is globally unique.
 */
@Getter
@Setter
@Entity
@Table(name = "wallet_operation", indexes = {
        @Index(name = "idx_operation_idempotent", columnList = "biz_request_id, operation_type", unique = true),
        @Index(name = "idx_operation_user_time", columnList = "user_id, created_at")
})
public class WalletOperationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "biz_request_id", nullable = false, length = 64)
    private String bizRequestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 32)
    private WalletOperationType operationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private OperationStatus status;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "counterparty_user_id")
    private Long counterpartyUserId;

    @Column(name = "currency", nullable = false, length = 16)
    private String currency;

    @Column(name = "amount", nullable = false, precision = 30, scale = 8)
    private BigDecimal amount;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "remark", length = 256)
    private String remark;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
