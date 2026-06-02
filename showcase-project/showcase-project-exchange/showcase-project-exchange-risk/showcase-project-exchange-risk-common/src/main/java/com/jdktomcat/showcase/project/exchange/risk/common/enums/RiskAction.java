package com.jdktomcat.showcase.project.exchange.risk.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RiskAction {

    REJECT("REJECT", "直接拒绝"),
    REVIEW("REVIEW", "人工审核"),
    ALERT("ALERT", "告警放行");

    private final String code;
    private final String label;

    public static RiskAction fromCode(String code) {
        for (RiskAction action : values()) {
            if (action.code.equals(code)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Unknown risk action: " + code);
    }
}
