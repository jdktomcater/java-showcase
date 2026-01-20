package com.jdktomcat.showcase.mybatis.plus.enums;

import com.jdktomcat.showcase.mybatis.plus.strategy.IdRetryHandleStrategy;
import com.jdktomcat.showcase.mybatis.plus.strategy.impl.DefaultIdRetryHandleStrategy;
import com.jdktomcat.showcase.mybatis.plus.strategy.impl.RedisTableRetryHandleStrategy;
import lombok.Getter;

/**
 * 重试处理器枚举类
 */
@Getter
public enum RetryHandlerEnum {
    /**
     * 默认重试生成策略
     */
    DEFAULT(1, new DefaultIdRetryHandleStrategy()),

    /**
     * redis重试生成策略
     */
    REDIS(2, new RedisTableRetryHandleStrategy());

    /**
     * 类型
     */
    private final Integer type;

    /**
     * id重试生成策略
     */
    private final IdRetryHandleStrategy idRetryHandleStrategy;

    /**
     * 构造器
     *
     * @param type                  类型编码
     * @param idRetryHandleStrategy 具体策略
     */
    RetryHandlerEnum(Integer type, IdRetryHandleStrategy idRetryHandleStrategy) {
        this.type = type;
        this.idRetryHandleStrategy = idRetryHandleStrategy;
    }

    /**
     * 根据类型获取对应枚举
     *
     * @param type 类型
     * @return 枚举
     */
    public static RetryHandlerEnum getInstance(Integer type) {
        for(RetryHandlerEnum retryHandlerEnum: RetryHandlerEnum.values()){
            if(retryHandlerEnum.getType().equals(type)){
                return retryHandlerEnum;
            }
        }
        return null;
    }
}
