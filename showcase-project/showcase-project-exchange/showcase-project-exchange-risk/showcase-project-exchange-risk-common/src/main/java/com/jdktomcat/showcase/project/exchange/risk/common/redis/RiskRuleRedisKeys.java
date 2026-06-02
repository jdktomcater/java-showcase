package com.jdktomcat.showcase.project.exchange.risk.common.redis;

/**
 * Redis key and channel names for exchange risk rule hot-reload.
 */
public final class RiskRuleRedisKeys {

    private RiskRuleRedisKeys() {
    }

    /** Full enabled-rules snapshot (JSON {@link RiskRuleRedisSnapshot}). */
    public static final String ENABLED_SNAPSHOT = "exchange:risk:rules:enabled:snapshot";

    /** Monotonic version string; consumers compare before reload. */
    public static final String VERSION = "exchange:risk:rules:version";

    /** Hash: field = ruleCode, value = rule JSON. */
    public static final String BY_CODE_HASH = "exchange:risk:rules:by-code";

    /** Pub/Sub channel for hot-reload notifications. */
    public static final String REFRESH_CHANNEL = "exchange:risk:rules:refresh";
}
