package com.jdktomcat.showcase.system.design.risk.service.engine.rule;

import com.jdktomcat.showcase.system.design.risk.service.engine.RiskContext;
import com.jdktomcat.showcase.system.design.risk.service.engine.RiskRule;
import com.jdktomcat.showcase.system.design.risk.service.engine.RuleResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * AML large-amount detection rule.
 *
 * <p>Mirrors real-world Currency Transaction Report (CTR) requirements and
 * Binance's internal withdrawal risk tiers:
 *
 * <pre>
 *   Single withdrawal ≥ $100,000 USD  → BLOCK  (terminal, requires manual approval)
 *   Single withdrawal ≥  $10,000 USD  → REVIEW (+40 pts, AML CTR threshold)
 *   Cumulative 24-hr  ≥  $50,000 USD  → REVIEW (+30 pts)
 *   KYC level 0 and amount ≥ $1,000   → REVIEW (+25 pts)
 * </pre>
 *
 * <p>Interview talking point: in production we would call an external oracle
 * (e.g. Chainalysis) to get on-chain risk scores for the destination address,
 * but that's done asynchronously in the Kafka consumer to avoid adding latency.
 */
@Component
public class AmountThresholdRule implements RiskRule {

    private static final BigDecimal HARD_BLOCK_USD     = new BigDecimal("100000");
    private static final BigDecimal CTR_THRESHOLD_USD  = new BigDecimal("10000");
    private static final BigDecimal DAILY_REVIEW_USD   = new BigDecimal("50000");
    private static final BigDecimal NO_KYC_LIMIT_USD   = new BigDecimal("1000");

    @Override
    public String name() {
        return "AmountThresholdRule";
    }

    @Override
    public int priority() {
        return 20;
    }

    @Override
    public RuleResult evaluate(RiskContext ctx) {
        if (!"WITHDRAWAL".equals(ctx.getEventType())) {
            return RuleResult.pass(name());
        }

        BigDecimal usd = ctx.getAmountUsd();
        if (usd == null) {
            return RuleResult.pass(name());
        }

        // Hard block: single withdrawal exceeds absolute ceiling
        if (usd.compareTo(HARD_BLOCK_USD) >= 0) {
            return RuleResult.block(name(), String.format("Single withdrawal $%.2f USD exceeds hard limit $%s", usd, HARD_BLOCK_USD));
        }

        int score = 0;
        StringBuilder reason = new StringBuilder();

        // CTR threshold
        if (usd.compareTo(CTR_THRESHOLD_USD) >= 0) {
            score += 40;
            reason.append(String.format("Single withdrawal $%.2f USD triggers AML CTR threshold ($%s); ", usd, CTR_THRESHOLD_USD));
        }

        // Cumulative 24-hr limit
        BigDecimal cumulative = ctx.getWithdrawal24HrUsd() != null ? ctx.getWithdrawal24HrUsd() : BigDecimal.ZERO;
        if (cumulative.compareTo(DAILY_REVIEW_USD) >= 0) {
            score += 30;
            reason.append(String.format("Cumulative 24-hr withdrawal $%.2f USD exceeds $%s; ", cumulative, DAILY_REVIEW_USD));
        }
        // No-KYC limit
        if (ctx.getKycLevel() == 0 && usd.compareTo(NO_KYC_LIMIT_USD) >= 0) {
            score += 25;
            reason.append(String.format("KYC level 0 user attempting $%.2f USD withdrawal (limit $%s); ", usd, NO_KYC_LIMIT_USD));
        }
        if (score > 0) {
            return RuleResult.review(name(), score, reason.toString().stripTrailing());
        }
        return RuleResult.pass(name());
    }
}
