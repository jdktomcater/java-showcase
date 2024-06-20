package com.jdktomcat.showcase.rocketmq.transaction.consumer.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户帐变记录信息
 */
@Data
@TableName("t_mac_record")
public class MacRecord implements Serializable {

    private static final long serialVersionUID = -4466829302339920271L;

    @TableId(value = "id", type = IdType.AUTO)
    Long id;

    @TableField("merchant_order_id")
    String merchantOrderId;

    @TableField("merchant_id")
    Integer merchantId;

    @TableField("type")
    Integer type;

    @TableField("pre")
    BigDecimal pre;

    @TableField("post")
    BigDecimal post;

    @TableField("pre_available")
    BigDecimal preAvailable;

    @TableField("post_available")
    BigDecimal postAvailable;

    @TableField("pre_frozen")
    BigDecimal preFrozen;

    @TableField("post_frozen")
    BigDecimal postFrozen;

    @TableField("create_time")
    Date createTime;

    @TableField("update_time")
    Date updateTime;
}
