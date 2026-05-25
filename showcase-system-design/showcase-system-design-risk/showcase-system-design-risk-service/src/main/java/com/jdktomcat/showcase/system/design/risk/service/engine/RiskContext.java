package com.jdktomcat.showcase.system.design.risk.service.engine;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Enriched context passed to each rule during evaluation.
 *
 * <p>Pre-fetched signals (blacklist membership, velocity counts, etc.) are resolved once
 * before the rule chain runs, so individual rules do not make redundant I/O calls.
 */
@Data
@Builder
public class RiskContext {

    // ── Raw request fields ──────────────────────────────────────────────────

    private String requestId;
    private Long userId;
    private String userIp;
    private String eventType;
    private String assetCode;
    private BigDecimal amount;
    private BigDecimal amountUsd;
    private String toAddress;
    private Map<String, String> metadata;

    // ── Pre-fetched enrichment signals ──────────────────────────────────────

    /** Whether the userId appears on the global blacklist. */
    private boolean userBlacklisted;

    /** Whether the source IP appears on the global blacklist. */
    private boolean ipBlacklisted;

    /** Whether the destination on-chain address appears on the sanctions / blacklist. */
    private boolean addressBlacklisted;

    /** Count of this user's events of the same type in the last 60 seconds. */
    private long velocityCount1Min;

    /** Count of this user's events of the same type in the last 3,600 seconds. */
    private long velocityCount1Hr;

    /** Count of this user's events of the same type in the last 86,400 seconds. */
    private long velocityCount24Hr;

    /**
     * Cumulative USD value of this user's WITHDRAWAL events in the last 24 hours.
     * Used for both large-amount detection and AML structuring detection.
     */
    private BigDecimal withdrawal24HrUsd;

    /** Account age in days; -1 if unknown. */
    private int accountAgeDays;

    /** KYC verification level: 0 = none, 1 = basic, 2 = full. */
    private int kycLevel;
}
