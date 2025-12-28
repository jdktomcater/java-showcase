package com.jdktomcat.showcase.mybatis.plus.enums;

/**
 * 重试处理器枚举类
 */
public enum RetryHandlerEnum {

    DEFAULT(1);

    private Integer type;

    RetryHandlerEnum(Integer type) {
        this.type = type;
    }




}
