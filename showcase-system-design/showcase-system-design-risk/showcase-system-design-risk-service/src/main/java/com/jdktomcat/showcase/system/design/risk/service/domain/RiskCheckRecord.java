package com.jdktomcat.showcase.system.design.risk.service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable audit record of every risk decision.
 * Written asynchronously after each risk check to avoid adding latency to the hot path.
 */
@Entity
@Table(name = "risk_check_record", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_request_id", columnList = "request_id", unique = true),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskCheckRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false, unique = true, length = 64)
    private String requestId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_ip", length = 64)
    private String userIp;

    @Column(name = "event_type", nullable = false, length = 32)
    private String eventType;

    @Column(name = "asset_code", length = 16)
    private String assetCode;

    @Column(name = "amount", precision = 30, scale = 8)
    private BigDecimal amount;

    @Column(name = "amount_usd", precision = 30, scale = 2)
    private BigDecimal amountUsd;

    @Column(name = "to_address", length = 128)
    private String toAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 16)
    private RiskLevel riskLevel;

    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    /** JSON array of rule names that were triggered. */
    @Column(name = "triggered_rules", columnDefinition = "TEXT")
    private String triggeredRules;

    @Column(name = "reason", length = 512)
    private String reason;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
