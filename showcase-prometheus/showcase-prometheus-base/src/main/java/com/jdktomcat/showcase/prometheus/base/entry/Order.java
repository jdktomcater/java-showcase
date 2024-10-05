package com.jdktomcat.showcase.prometheus.base.entry;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 订单流水
     */
    private String no;

    /**
     * 创建时间
     */
    private long create;

}
