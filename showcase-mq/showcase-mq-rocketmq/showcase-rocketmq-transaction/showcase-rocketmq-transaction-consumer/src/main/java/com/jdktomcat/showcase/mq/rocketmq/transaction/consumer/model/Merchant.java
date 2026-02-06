package com.jdktomcat.showcase.mq.rocketmq.transaction.consumer.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商戶信息
 */
@Data
@TableName("`t_merchant`")
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户ID
     */
    @TableId
    private Integer id;

    /**
     * 1：启用，0:停用，-1:软删除
     */
    private Integer status;

    /**
     * 商户类型 1 内部 2 外部
     */
    private Integer type;

    /**
     * 商户代码
     */
    private String code;

    /**
     * 商户名称
     */
    private String name;

    /**
     * 总余额
     */
    private BigDecimal balance;

    /**
     * 可用余额
     */
    private BigDecimal available;

    /**
     * 冻结余额
     */
    private BigDecimal frozen;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 商户服务费
     */
    private BigDecimal fee;

    /**
     * 最近更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private String createTime;
}
