package com.jdktomcat.showcase.rocketmq.transaction.producer.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "`t_third_withdraw_order`", autoResultMap = true)
public class ThirdWithdrawOrder implements Serializable {

    private static final long serialVersionUID = 1L;


    public ThirdWithdrawOrder(){
    }

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
    protected Integer orderStatus;

    /**
     * 支付方式
     */
    @TableField("pay_type")
    protected Integer payType;

    /**
     * 下单金额
     */
    @TableField("pay_amount")
    protected BigDecimal payAmount;

    /**
     * 渠道费
     */
    @TableField("fee")
    protected BigDecimal fee;

    /**
     * 0:商户订单，1:超额内转订单，2低额内转订单，3，充值测试转账订单,4提款测试转账订单,5卡异常转出金额,100父订单
     */
    @TableField("order_type")
    private Integer OrderType;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

}
