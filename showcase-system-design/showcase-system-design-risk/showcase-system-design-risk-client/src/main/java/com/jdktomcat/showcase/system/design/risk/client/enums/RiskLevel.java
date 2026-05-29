package com.jdktomcat.showcase.system.design.risk.client.enums;

/**
 * Risk decision levels returned by the risk engine.
 *
 * <p>PASS   – request is clean, proceed immediately.
 * <p>REVIEW – request is suspicious; route to manual review queue before execution.
 * <p>BLOCK  – request is high-risk; reject immediately and trigger alerts.
 */
public enum RiskLevel {

    PASS(0, "Pass"),
    REVIEW(50, "Review"),
    BLOCK(100, "Block");

    private final int score;
    private final String label;

    RiskLevel(int score, String label) {
        this.score = score;
        this.label = label;
    }

    public int getScore() {
        return score;
    }

    public String getLabel() {
        return label;
    }

    public boolean isHigherThan(RiskLevel other) {
        return this.score > other.score;
    }
}
