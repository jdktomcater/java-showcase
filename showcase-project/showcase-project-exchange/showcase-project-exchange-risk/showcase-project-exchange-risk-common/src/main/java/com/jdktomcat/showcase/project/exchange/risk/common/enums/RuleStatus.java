package com.jdktomcat.showcase.project.exchange.risk.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RuleStatus {

    ENABLED("ENABLED", "启用"),
    DISABLED("DISABLED", "停用");

    private final String code;
    private final String label;

    public static RuleStatus fromCode(String code) {
        for (RuleStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown rule status: " + code);
    }
}
