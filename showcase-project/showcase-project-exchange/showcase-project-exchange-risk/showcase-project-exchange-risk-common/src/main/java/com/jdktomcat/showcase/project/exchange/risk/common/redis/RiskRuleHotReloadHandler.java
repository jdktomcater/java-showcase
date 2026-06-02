package com.jdktomcat.showcase.project.exchange.risk.common.redis;

/**
 * Helper for downstream services (matching engine, order gateway) to consume hot-reloaded rules.
 *
 * <p>Typical integration:
 * <ol>
 *   <li>On startup, read {@link RiskRuleRedisKeys#ENABLED_SNAPSHOT} and load rules into memory.</li>
 *   <li>Subscribe to {@link RiskRuleRedisKeys#REFRESH_CHANNEL}.</li>
 *   <li>On message, compare {@link RiskRuleRefreshMessage#getVersion()} with local version;
 *       if newer, reload snapshot and swap in-memory rule set atomically.</li>
 * </ol>
 */
public interface RiskRuleHotReloadHandler {

    /** Load initial rules or perform full reload from Redis snapshot. */
    void reloadRules(RiskRuleRedisSnapshot snapshot);

    /** Optional hook after pub/sub notification before reload. */
    default void onRefreshSignal(RiskRuleRefreshMessage message) {
        // default no-op
    }
}
