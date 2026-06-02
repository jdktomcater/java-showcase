package com.jdktomcat.showcase.project.exchange.risk.admin.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "exchange_risk_rule", indexes = {
        @Index(name = "idx_rule_code", columnList = "rule_code", unique = true),
        @Index(name = "idx_rule_type_status", columnList = "rule_type, status"),
        @Index(name = "idx_scope_target", columnList = "scope, target_user_id, target_symbol")
})
public class RiskRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_code", nullable = false, length = 64, unique = true)
    private String ruleCode;

    @Column(name = "rule_name", nullable = false, length = 128)
    private String ruleName;

    @Column(name = "rule_type", nullable = false, length = 32)
    private String ruleType;

    @Column(name = "scope", nullable = false, length = 32)
    private String scope;

    @Column(name = "target_user_id", length = 64)
    private String targetUserId;

    @Column(name = "target_symbol", length = 32)
    private String targetSymbol;

    @Column(name = "threshold_value", precision = 30, scale = 8)
    private BigDecimal thresholdValue;

    @Column(name = "threshold_config", columnDefinition = "TEXT")
    private String thresholdConfig;

    @Column(name = "action", nullable = false, length = 16)
    private String action;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "status", nullable = false, length = 16)
    private String status;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @Column(name = "updated_by", length = 64)
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
