package com.jdktomcat.showcase.system.design.risk.service.engine.rule;

import com.jdktomcat.showcase.system.design.risk.service.engine.RiskContext;
import com.jdktomcat.showcase.system.design.risk.service.engine.RiskRule;
import com.jdktomcat.showcase.system.design.risk.service.engine.RuleResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * AML structuring (a.k.a. "smurfing") detection rule.
 *
 * <p>Structuring is the deliberate splitting of large transactions into smaller ones
 * to stay below AML reporting thresholds.  A classic pattern: 5 × $9,999 withdrawals
 * in 24 hours instead of one $50,000 withdrawal.
 *
 * <p>Detection heuristic used here:
 * <pre>
 *   ≥ 3 withdrawals in 24 hrs  AND
 *   each individual amount     &lt; $10,000 USD  AND
 *   cumulative 24-hr amount    ≥  $8,000 USD
 *   → REVIEW (+50 pts)
 * </pre>
 *
 * <p>Interview talking point: this heuristic has a high false-positive rate.
 * In production, a second-pass ML model (graph neural net on transaction graphs,
 * or sequence model on time-series) runs asynchronously via the Kafka consumer
 * and can escalate to BLOCK after deeper analysis.  The synchronous rule only
 * flags for human review to keep p99 latency under 50 ms.
 */
@Component
public class StructuringDetectionRule implements RiskRule {

    private static final BigDecimal CTR_THRESHOLD_USD       = new BigDecimal("10000");
    private static final BigDecimal CUMULATIVE_SUSPECT_USD  = new BigDecimal("8000");
    private static final long       MIN_TX_COUNT            = 3;

    @Override
    public String name() {
        return "StructuringDetectionRule";
    }

    @Override
    public int priority() {
        return 30;
    }

    @Override
    public RuleResult evaluate(RiskContext ctx) {
        if (!"WITHDRAWAL".equals(ctx.getEventType())) {
            return RuleResult.pass(name());
        }

        BigDecimal singleUsd     = ctx.getAmountUsd();
        BigDecimal cumulativeUsd = ctx.getWithdrawal24HrUsd() != null
                ? ctx.getWithdrawal24HrUsd() : BigDecimal.ZERO;
        long txCount24Hr         = ctx.getVelocityCount24Hr();

        boolean belowCtr         = singleUsd != null && singleUsd.compareTo(CTR_THRESHOLD_USD) < 0;
        boolean highCumulative   = cumulativeUsd.compareTo(CUMULATIVE_SUSPECT_USD) >= 0;
        boolean manyTransactions = txCount24Hr >= MIN_TX_COUNT;

        if (belowCtr && highCumulative && manyTransactions) {
            return RuleResult.review(name(), 50,
                    String.format("Potential structuring: %d withdrawals in 24hr, "
                                    + "cumulative $%.2f USD with each below CTR threshold $%s",
                            txCount24Hr, cumulativeUsd, CTR_THRESHOLD_USD));
        }
        return RuleResult.pass(name());
    }
}
