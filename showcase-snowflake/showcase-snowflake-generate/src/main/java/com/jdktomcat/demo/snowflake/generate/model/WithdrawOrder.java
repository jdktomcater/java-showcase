package com.jdktomcat.demo.snowflake.generate.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("`t_withdraw_order`")
public class WithdrawOrder {

    @TableId(value = "id")
    private Long id;

    @TableField(value = "order_id")
    private String orderId;

    @TableField(value = "merchant_order_id")
    private String merchantOrderId;

    @TableField(value = "order_status")
    private int orderStatus;

    @TableField(value = "priority_match_status")
    private int priorityMatchStatus;

    @TableField(value = "parent_order_status")
    private int parentOrderStatus;

    @TableField(value = "create_time")
    private Date createTime;

}
