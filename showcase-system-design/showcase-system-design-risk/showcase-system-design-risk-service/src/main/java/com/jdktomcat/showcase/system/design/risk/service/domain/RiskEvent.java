package com.jdktomcat.showcase.system.design.risk.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Kafka message published after every non-PASS risk decision.
 * Consumed by async processors: ML scoring, fraud pattern analysis, account freeze triggers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvent {

    private String requestId;
    private Long userId;
    private String userIp;
    private String eventType;
    private String assetCode;
    private BigDecimal amount;
    private BigDecimal amountUsd;
    private String toAddress;

    private RiskLevel riskLevel;
    private Integer riskScore;
    private List<String> triggeredRules;
    private String reason;

    /** Epoch millis of when the risk check occurred. */
    private long eventTimestamp;

    public static RiskEvent of(String requestId, Long userId, String userIp,
                               String eventType, String assetCode,
                               BigDecimal amount, BigDecimal amountUsd,
                               String toAddress, RiskLevel riskLevel,
                               Integer riskScore, List<String> triggeredRules,
                               String reason) {
        return RiskEvent.builder()
                .requestId(requestId)
                .userId(userId)
                .userIp(userIp)
                .eventType(eventType)
                .assetCode(assetCode)
                .amount(amount)
                .amountUsd(amountUsd)
                .toAddress(toAddress)
                .riskLevel(riskLevel)
                .riskScore(riskScore)
                .triggeredRules(triggeredRules)
                .reason(reason)
                .eventTimestamp(Instant.now().toEpochMilli())
                .build();
    }
}
