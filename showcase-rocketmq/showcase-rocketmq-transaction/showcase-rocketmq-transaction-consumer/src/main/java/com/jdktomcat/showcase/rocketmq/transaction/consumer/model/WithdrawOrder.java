package com.jdktomcat.showcase.rocketmq.transaction.consumer.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("`t_withdraw_order`")
public class WithdrawOrder implements Serializable {

    private static final long serialVersionUID = -1021319405726511752L;

    /**
     * 订单索引
     */
    @TableId("order_index")
    protected Long orderIndex;

    /**
     * 订单号
     */
    @TableField("order_id")
    protected String orderId;

    /**
     * 商户订单号最长64位
     */
    @TableField("merchant_order_id")
    protected String merchantOrderId;

    /**
     * 商户编号
     */
    @TableField("merchant_id")
    protected Integer merchantId;

    /**
     * 订单状态
     */
    @TableField("status")
    protected Integer status;

    /**
     * 支付方式
     */
    @TableField("pay_type")
    protected Integer payType;

    /**
     * 下单金额
     */
    @TableField("amount")
    protected BigDecimal amount;

    /**
     * 渠道费
     */
    @TableField("fee")
    protected BigDecimal fee;

    /**
     * 订单类型
     */
    @TableField("type")
    private Integer orderType;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
}
