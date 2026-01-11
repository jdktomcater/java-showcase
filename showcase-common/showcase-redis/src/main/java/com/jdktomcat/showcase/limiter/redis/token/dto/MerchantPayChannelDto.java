package com.jdktomcat.showcase.limiter.redis.token.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 实体类
 */
@Data
public class MerchantPayChannelDto {
    /**
     * 商户ID
     */
    private Integer merchantId;
    /**
     * 支付类型
     */
    private Integer payType;
    /**
     * 支付渠道名称
     */
    private String payChannelName;
    /**
     * 最小值
     */
    private BigDecimal minAmountPerTrans;
    /**
     * 最大值
     */
    private BigDecimal maxAmountPerTrans;

    /**
     * PC固定金额
     */
    private String pcFixedAmount;
    /**
     * WAP固定金额
     */
    private String wapFixedAmount;
    /**
     * APP固定金额
     */
    private String appFixedAmount;
    /**
     * IOS固定金额
     */
    private String iosFixedAmount;
    /**
     * ANDROID固定金额
     */
    private String androidFixedAmount;
    /**
     * 协议
     */
    private String protocol;
    /**
     * 支付渠道ID
     */
    private Integer payChannelID;
    /**
     * 币种
     */
    private String currency;
    /**
     * 是否为极速
     */
    private Integer IsFastPay;
}

