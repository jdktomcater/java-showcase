package com.jdktomcat.showcase.system.design.risk.service.engine;

/**
 * Strategy interface for a single risk rule.
 *
 * <p>Rules are Spring beans collected by {@link RiskEngine} at startup via
 * {@code List<RiskRule>} injection.  Ordering by {@link #priority()} determines
 * short-circuit evaluation order: lower number = evaluated first.
 *
 * <p>Rules that are definitively blocking (blacklist, sanctions) should have the
 * lowest priority numbers so they terminate the chain immediately and avoid
 * unnecessary Redis / DB calls by downstream rules.
 */
public interface RiskRule {

    /** Unique name used in audit logs and triggered-rules lists. */
    String name();

    /**
     * Execution priority; lower = runs first.
     * Suggested ranges:
     * <ul>
     *   <li>1–10   : hard-block rules (blacklist, sanctions)</li>
     *   <li>11–50  : velocity / rate-limiting rules</li>
     *   <li>51–100 : soft-signal rules (amount threshold, account age, KYC)</li>
     * </ul>
     */
    int priority();

    /** Evaluate the rule against the enriched context and return a result. */
    RuleResult evaluate(RiskContext context);
}
