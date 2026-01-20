package com.jdktomcat.showcase.mybatis.plus.entry;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 渠道账变记录表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_channelaccountrecord")
public class ChannelAccountRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 平台订单索引
     */
    @TableId(value = "OrderIndex")
    private Long OrderIndex;

    /**
     * 订单标识
     */
    @TableField(value = "OrderID")
    private String OrderID;

    /**
     * 渠道ID
     */
    @TableField(value = "PayChannelID")
    private Long PayChannelID;

    /**
     * 创建时间
     */
    @TableField(value = "CreateTime")
    private Date CreateTime;
}
