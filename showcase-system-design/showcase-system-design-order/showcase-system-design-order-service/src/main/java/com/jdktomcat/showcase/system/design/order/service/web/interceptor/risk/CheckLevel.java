package com.jdktomcat.showcase.system.design.order.service.web.interceptor.risk;

/**
 * 风险等级
 */
public enum CheckLevel {
    /**
     * 高风险
     */
    HIGH(0),

    /**
     * 中风险
     */
    MEDIUM(1),

    /**
     * 低风险
     */
    LOW(2);
    ;

    private final int value;

    CheckLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
