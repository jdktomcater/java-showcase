package com.jdktomcat.showcase.system.design.risk.service.engine.rule;

import com.jdktomcat.showcase.system.design.risk.service.engine.RiskContext;
import com.jdktomcat.showcase.system.design.risk.service.engine.RiskRule;
import com.jdktomcat.showcase.system.design.risk.service.engine.RuleResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * New-account high-risk rule.
 *
 * <p>Newly created accounts attempting significant withdrawals are a common
 * account-takeover and money-mule pattern.  An attacker typically registers an
 * account, deposits funds (often through stolen payment methods), and immediately
 * tries to withdraw to an external wallet.
 *
 * <p>Thresholds:
 * <pre>
 *   Account age &lt; 1 day  AND withdrawal ≥ $500  → BLOCK  (terminal)
 *   Account age &lt; 7 days AND withdrawal ≥ $200  → REVIEW (+35 pts)
 * </pre>
 */
@Component
public class NewAccountRule implements RiskRule {

    private static final int     NEW_ACCOUNT_HARD_DAYS     = 1;
    private static final int     NEW_ACCOUNT_SOFT_DAYS     = 7;
    private static final BigDecimal HARD_LIMIT_USD         = new BigDecimal("500");
    private static final BigDecimal SOFT_LIMIT_USD         = new BigDecimal("200");

    @Override
    public String name() {
        return "NewAccountRule";
    }

    @Override
    public int priority() {
        return 40;
    }

    @Override
    public RuleResult evaluate(RiskContext ctx) {
        if (!"WITHDRAWAL".equals(ctx.getEventType())) {
            return RuleResult.pass(name());
        }
        int ageDays = ctx.getAccountAgeDays();
        if (ageDays < 0) {
            return RuleResult.pass(name());
        }

        BigDecimal usd = ctx.getAmountUsd();
        if (usd == null) {
            return RuleResult.pass(name());
        }

        if (ageDays < NEW_ACCOUNT_HARD_DAYS && usd.compareTo(HARD_LIMIT_USD) >= 0) {
            return RuleResult.block(name(),
                    String.format("Account age %d day(s) attempting $%.2f USD withdrawal; "
                            + "new-account hard limit is $%s", ageDays, usd, HARD_LIMIT_USD));
        }
        if (ageDays < NEW_ACCOUNT_SOFT_DAYS && usd.compareTo(SOFT_LIMIT_USD) >= 0) {
            return RuleResult.review(name(), 35,
                    String.format("Account age %d day(s) attempting $%.2f USD withdrawal; "
                            + "new-account soft limit is $%s", ageDays, usd, SOFT_LIMIT_USD));
        }
        return RuleResult.pass(name());
    }
}
