package com.jdktomcat.showcase.mybatis.plus.enums;

import com.jdktomcat.showcase.mybatis.plus.strategy.IdRetryHandleStrategy;
import com.jdktomcat.showcase.mybatis.plus.strategy.impl.DefaultIdRetryHandleStrategy;
import com.jdktomcat.showcase.mybatis.plus.strategy.impl.RedisTableRetryHandleStrategy;

/**
 * 重试处理器枚举类
 */

public enum RetryHandlerEnum {

    DEFAULT(1, new DefaultIdRetryHandleStrategy()),
    REDIS(2, new RedisTableRetryHandleStrategy());

    private final Integer type;
    private final IdRetryHandleStrategy idRetryHandleStrategy;

    public IdRetryHandleStrategy getIdRetryHandleStrategy() {
        return idRetryHandleStrategy;
    }

    public Integer getType() {
        return type;
    }

    RetryHandlerEnum(Integer type, IdRetryHandleStrategy idRetryHandleStrategy) {
        this.type = type;
        this.idRetryHandleStrategy = idRetryHandleStrategy;
    }

    public static RetryHandlerEnum getInstance(Integer type) {
        for(RetryHandlerEnum retryHandlerEnum: RetryHandlerEnum.values()){
            if(retryHandlerEnum.getType().equals(type)){
                return retryHandlerEnum;
            }
        }
        return null;
    }
}
