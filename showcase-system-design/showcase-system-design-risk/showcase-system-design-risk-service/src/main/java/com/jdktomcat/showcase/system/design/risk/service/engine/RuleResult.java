package com.jdktomcat.showcase.system.design.risk.service.engine;

import com.jdktomcat.showcase.system.design.risk.client.enums.RiskLevel;
import lombok.Builder;
import lombok.Data;

/**
 * Output produced by a single {@link RiskRule} evaluation.
 */
@Data
@Builder
public class RuleResult {

    private String ruleName;
    private RiskLevel level;

    /**
     * Additive score contribution from this rule (0–100).
     * The engine accumulates scores across all rules and clamps to 100.
     */
    private int scoreContribution;

    private String reason;

    /**
     * When {@code true} the engine stops evaluating further rules immediately.
     * Typically set to {@code true} only for hard-BLOCK rules (e.g. blacklist hit).
     */
    private boolean terminal;

    public static RuleResult pass(String ruleName) {
        return RuleResult.builder()
                .ruleName(ruleName)
                .level(RiskLevel.PASS)
                .scoreContribution(0)
                .terminal(false)
                .build();
    }

    public static RuleResult review(String ruleName, int score, String reason) {
        return RuleResult.builder()
                .ruleName(ruleName)
                .level(RiskLevel.REVIEW)
                .scoreContribution(score)
                .reason(reason)
                .terminal(false)
                .build();
    }

    public static RuleResult block(String ruleName, String reason) {
        return RuleResult.builder()
                .ruleName(ruleName)
                .level(RiskLevel.BLOCK)
                .scoreContribution(100)
                .reason(reason)
                .terminal(true)
                .build();
    }
}
