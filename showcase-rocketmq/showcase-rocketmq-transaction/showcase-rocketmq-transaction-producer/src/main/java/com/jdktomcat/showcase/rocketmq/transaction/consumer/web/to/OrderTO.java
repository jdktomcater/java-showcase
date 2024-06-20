package com.jdktomcat.showcase.rocketmq.transaction.consumer.web.to;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单参数信息
 */
@Data
public class OrderTO implements Serializable {

    private static final long serialVersionUID = -1758967799297014054L;

    private String merchantOrderId;

    private Integer merchantId;

    private BigDecimal amount;
}
