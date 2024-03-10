package com.jdktomcat.demo.dead.lock.restart.transaction.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("`t_withdraw_order`")
public class WithdrawOrder {

    @TableId(value = "OrderIndex")
    private Long orderIndex;

    @TableField(value = "OrderId")
    private String orderId;

    @TableField(value = "MerchantOrderId")
    private String merchantOrderId;

    @TableField(value = "OrderStatus")
    private int orderStatus;

    @TableField(value = "PriorityMatchStatus")
    private int priorityMatchStatus;

    @TableField(value = "ParentOrderStatus")
    private int parentOrderStatus;

    @TableField(value = "CreateTime")
    private Date createTime;

}
