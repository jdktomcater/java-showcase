package com.jdktomcat.showcase.data.sharding.jdbc.entry;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 充值订单
 *
 * @author yuhata
 * @since 2025-12-29
 */
@Data
@TableName(value="t_depositorder", autoResultMap = true)
public class DepositOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单索引
     */
    @TableId("OrderIndex")
    protected Long orderIndex;

    /**
     * 订单号
     */
    @TableField("OrderID")
    protected String orderId;


    /**
     * 下单金额
     */
    @TableField("PayAmount")
    protected BigDecimal payAmount;
}
