package com.jdktomcat.showcase.system.design.risk.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Request DTO for the synchronous risk-check endpoint.
 *
 * <p>Callers (payment-service, order-service, etc.) send this payload before
 * executing any financial operation.  The risk service responds with a
 * {@link RiskCheckResponse} containing a go/no-go decision.
 *
 * <p>All monetary amounts must be expressed in the native asset ({@code amount})
 * AND their USD equivalent ({@code amountUsd}) so that rules can apply
 * currency-agnostic thresholds without performing exchange-rate lookups at
 * check time (the caller already has access to the oracle price).
 */
@Data
public class RiskCheckRequest {

    /**
     * Globally unique identifier for this risk-check call.
     * Callers must ensure idempotency: the same {@code requestId} always
     * produces the same risk decision (cached in Redis for 24 h).
     */
    @NotBlank
    private String requestId;

    @NotNull
    private Long userId;

    /** Source IPv4 or IPv6 address of the end-user. */
    private String userIp;

    /**
     * Type of event being risk-checked.
     * Well-known values: WITHDRAWAL, TRADE, LOGIN, TRANSFER, DEPOSIT.
     */
    @NotBlank
    private String eventType;

    /** Asset symbol, e.g. BTC, ETH, USDT. */
    private String assetCode;

    /** Amount in the native asset unit (e.g. 0.5 BTC). */
    private BigDecimal amount;

    /** USD equivalent of {@code amount} at current market price. */
    private BigDecimal amountUsd;

    /** On-chain destination address (withdrawals only). */
    private String toAddress;

    /**
     * Extensible metadata bag for context that does not fit the fixed schema.
     * Examples:
     * <ul>
     *   <li>{@code "accountAgeDays"} → account age in days (string-encoded int)</li>
     *   <li>{@code "kycLevel"}       → KYC verification level: 0, 1, or 2</li>
     *   <li>{@code "deviceId"}       → device fingerprint for device-risk scoring</li>
     * </ul>
     */
    private Map<String, String> metadata;
}
