package com.jdktomcat.showcase.project.exchange.risk.admin.event;

/**
 * Published after a risk rule mutation succeeds in DB.
 */
public record RiskRuleChangedEvent(String changeType, String ruleCode, String operator) {
}
