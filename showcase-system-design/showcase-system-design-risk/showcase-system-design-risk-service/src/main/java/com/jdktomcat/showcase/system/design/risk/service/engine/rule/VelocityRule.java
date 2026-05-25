package com.jdktomcat.showcase.system.design.risk.service.engine.rule;

import com.jdktomcat.showcase.system.design.risk.service.engine.RiskContext;
import com.jdktomcat.showcase.system.design.risk.service.engine.RiskRule;
import com.jdktomcat.showcase.system.design.risk.service.engine.RuleResult;
import org.springframework.stereotype.Component;

/**
 * Velocity rule: detects abnormally high transaction frequency per user or IP.
 *
 * <p>Velocity counts are pre-fetched from the Redis sliding-window limiter by
 * {@code RiskCheckService} and stored in {@link RiskContext} before rule evaluation,
 * so this rule is pure in-memory arithmetic (no I/O).
 *
 * <p>Thresholds (tuned for crypto-exchange withdrawal traffic):
 * <pre>
 *   WITHDRAWAL  > 5 / min   → REVIEW (+30 pts)
 *   WITHDRAWAL  > 20 / hr   → REVIEW (+20 pts)
 *   Any event   > 100 / min → BLOCK  (terminal)
 *   IP          > 200 / min → BLOCK  (terminal, credential stuffing / bot)
 * </pre>
 *
 * <p>Interview talking point: why sliding window over fixed window?
 * A fixed window allows 2× the limit at the boundary (e.g., 10 req at 00:59:59 and
 * 10 more at 01:00:01).  The sliding window has no such boundary burst vulnerability.
 */
@Component
public class VelocityRule implements RiskRule {

    private static final long WITHDRAWAL_PER_MIN_REVIEW  = 5;
    private static final long WITHDRAWAL_PER_HR_REVIEW   = 20;
    private static final long ANY_EVENT_PER_MIN_BLOCK    = 100;
    private static final long IP_PER_MIN_BLOCK           = 200;

    @Override
    public String name() {
        return "VelocityRule";
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public RuleResult evaluate(RiskContext ctx) {
        // Hard blocks first (terminal)
        if (ctx.getVelocityCount1Min() > ANY_EVENT_PER_MIN_BLOCK) {
            return RuleResult.block(name(),
                    String.format("User %d exceeded %d events/min (actual: %d)",
                            ctx.getUserId(), ANY_EVENT_PER_MIN_BLOCK, ctx.getVelocityCount1Min()));
        }

        // Aggregate score from soft signals
        int score = 0;
        StringBuilder reason = new StringBuilder();

        if ("WITHDRAWAL".equals(ctx.getEventType())) {
            if (ctx.getVelocityCount1Min() > WITHDRAWAL_PER_MIN_REVIEW) {
                score += 30;
                reason.append(String.format("withdrawal velocity %d/min exceeds %d; ",
                        ctx.getVelocityCount1Min(), WITHDRAWAL_PER_MIN_REVIEW));
            }
            if (ctx.getVelocityCount1Hr() > WITHDRAWAL_PER_HR_REVIEW) {
                score += 20;
                reason.append(String.format("withdrawal velocity %d/hr exceeds %d; ",
                        ctx.getVelocityCount1Hr(), WITHDRAWAL_PER_HR_REVIEW));
            }
        }

        if (score > 0) {
            return RuleResult.review(name(), score, reason.toString().stripTrailing());
        }
        return RuleResult.pass(name());
    }
}
