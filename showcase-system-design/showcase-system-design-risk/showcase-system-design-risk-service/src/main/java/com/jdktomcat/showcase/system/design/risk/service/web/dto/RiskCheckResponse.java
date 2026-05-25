package com.jdktomcat.showcase.system.design.risk.service.web.dto;

import com.jdktomcat.showcase.system.design.risk.service.domain.RiskLevel;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response DTO returned by the synchronous risk-check endpoint.
 *
 * <p>Callers must act on {@link #riskLevel}:
 * <ul>
 *   <li>{@code PASS}   – proceed with the operation.</li>
 *   <li>{@code REVIEW} – hold the operation and route to manual review queue.</li>
 *   <li>{@code BLOCK}  – reject the operation immediately; do not execute.</li>
 * </ul>
 */
@Data
@Builder
public class RiskCheckResponse {

    private String requestId;

    /** Final risk decision. */
    private RiskLevel riskLevel;

    /** Cumulative risk score 0–100. */
    private int riskScore;

    /** Names of rules that contributed a non-zero score. */
    private List<String> triggeredRules;

    /** Human-readable concatenation of reasons from triggered rules. */
    private String reason;

    /** Wall-clock time from request receipt to response serialisation (ms). */
    private long processingTimeMs;
}
