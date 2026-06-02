package com.jdktomcat.showcase.project.exchange.risk.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RiskScope {

    GLOBAL("GLOBAL", "全平台"),
    USER("USER", "指定用户"),
    SYMBOL("SYMBOL", "指定交易对"),
    USER_SYMBOL("USER_SYMBOL", "用户+交易对");

    private final String code;
    private final String label;

    public static RiskScope fromCode(String code) {
        for (RiskScope scope : values()) {
            if (scope.code.equals(code)) {
                return scope;
            }
        }
        throw new IllegalArgumentException("Unknown risk scope: " + code);
    }
}
