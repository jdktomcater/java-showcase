package com.jdktomcat.showcase.data.mybatis.plus.strategy;

import org.apache.ibatis.plugin.Invocation;

/**
 * 主键生成策略
 */
public interface IdRetryHandleStrategy {

    /**
     * 主键重新生成策略
     *
     * @param invocation 调用链
     */
    void handle(Invocation invocation) throws IllegalAccessException ;
}
