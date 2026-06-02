package com.jdktomcat.showcase.project.exchange.risk.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Exchange risk rule categories used by the matching / order gateway.
 */
@Getter
@RequiredArgsConstructor
public enum RiskRuleType {

    ORDER_SIZE_LIMIT("ORDER_SIZE_LIMIT", "单笔下单数量上限"),
    DAILY_VOLUME_LIMIT("DAILY_VOLUME_LIMIT", "日累计成交量上限"),
    ORDER_FREQUENCY("ORDER_FREQUENCY", "下单频率限制"),
    PRICE_DEVIATION("PRICE_DEVIATION", "价格偏离度限制"),
    POSITION_LIMIT("POSITION_LIMIT", "持仓上限"),
    SYMBOL_RESTRICTION("SYMBOL_RESTRICTION", "交易对黑白名单"),
    USER_BLACKLIST("USER_BLACKLIST", "用户黑名单");

    private final String code;
    private final String label;

    public static RiskRuleType fromCode(String code) {
        for (RiskRuleType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown risk rule type: " + code);
    }
}
