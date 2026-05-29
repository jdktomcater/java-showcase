package com.jdktomcat.showcase.system.design.risk.service.engine;

import com.jdktomcat.showcase.system.design.risk.client.enums.RiskLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Core risk evaluation engine.
 *
 * <p>Executes all registered {@link RiskRule} beans in priority order using a
 * <em>short-circuit chain of responsibility</em> pattern:
 * <ul>
 *   <li>Rules that return {@link RuleResult#isTerminal()} = {@code true} immediately
 *       stop the chain — no further rules are evaluated.</li>
 *   <li>Scores from non-terminal rules are accumulated; the final risk level is
 *       derived from the total score.</li>
 * </ul>
 *
 * <p>Score thresholds:
 * <pre>
 *   score &lt; 50  → PASS
 *   50 ≤ score &lt; 80 → REVIEW
 *   score ≥ 80   → BLOCK
 * </pre>
 *
 * <p>Design rationale (common interview question): separating the engine from the
 * rules allows hot-swap of rule logic, A/B testing of new rules, and independent
 * unit-testing without wiring the full Spring context.
 */
@Slf4j
@Component
public class RiskEngine {

    private static final int REVIEW_THRESHOLD = 50;
    private static final int BLOCK_THRESHOLD  = 80;

    private final List<RiskRule> rules;

    /**
     * Spring collects all {@link RiskRule} beans and injects them here.
     * We sort by priority at construction time so the runtime evaluation loop is O(n).
     */
    public RiskEngine(List<RiskRule> rules) {
        this.rules = rules.stream().sorted(Comparator.comparingInt(RiskRule::priority)).toList();
        log.info("RiskEngine initialised with {} rules: {}", this.rules.size(), this.rules.stream().map(RiskRule::name).toList());
    }

    /**
     * Evaluate all applicable rules against the enriched context.
     *
     * @return aggregated {@link EngineResult} containing the final risk level,
     *         cumulative score, and list of triggered rule names + reasons.
     */
    public EngineResult evaluate(RiskContext context) {
        int totalScore = 0;
        List<String> triggeredRules = new ArrayList<>();
        List<String> reasons = new ArrayList<>();
        RiskLevel highestLevel = RiskLevel.PASS;
        for (RiskRule rule : rules) {
            RuleResult result;
            try {
                result = rule.evaluate(context);
            } catch (Exception e) {
                // Fail open: a buggy rule must not take down the entire risk check.
                // Log the error and continue with the next rule.
                log.error("Rule [{}] threw an exception; skipping. requestId={}", rule.name(), context.getRequestId(), e);
                continue;
            }
            if (result.getLevel() != RiskLevel.PASS) {
                triggeredRules.add(rule.name());
                if (result.getReason() != null) {
                    reasons.add(result.getReason());
                }
                totalScore = Math.min(100, totalScore + result.getScoreContribution());
                if (result.getLevel().isHigherThan(highestLevel)) {
                    highestLevel = result.getLevel();
                }
            }
            if (result.isTerminal()) {
                log.debug("Terminal rule [{}] hit; stopping chain. requestId={}", rule.name(), context.getRequestId());
                break;
            }
        }

        RiskLevel finalLevel = deriveLevel(totalScore, highestLevel);
        String reason = String.join("; ", reasons);
        return EngineResult.builder()
                .riskLevel(finalLevel)
                .riskScore(totalScore)
                .triggeredRules(triggeredRules)
                .reason(reason)
                .build();
    }

    private RiskLevel deriveLevel(int score, RiskLevel highestFromRules) {
        // A terminal BLOCK rule forces level to BLOCK regardless of accumulated score.
        if (highestFromRules == RiskLevel.BLOCK) {
            return RiskLevel.BLOCK;
        }
        if (score >= BLOCK_THRESHOLD) {
            return RiskLevel.BLOCK;
        }
        if (score >= REVIEW_THRESHOLD) {
            return RiskLevel.REVIEW;
        }
        return RiskLevel.PASS;
    }

    // ── Inner result DTO ────────────────────────────────────────────────────

    @lombok.Data
    @lombok.Builder
    public static class EngineResult {
        private RiskLevel riskLevel;
        private int riskScore;
        private List<String> triggeredRules;
        private String reason;
    }
}
