package com.jdktomcat.showcase.legacy.system.component.httpclient.context;

import lombok.Data;

@Data
public class FastChannelDTO {
    /**
     * 三方商户编码
     */
    private long merchantId;
    /**
     * 三方商户编码
     */
    private String merchantCode;
    /**
     * 渠道ID
     */
    private int channelId;
    /**
     * 三方通道编码:商户渠道号
     */
    private String channelMerchantId;
    /**
     * 支付方式
     */
    private int payType;

    /**
     * 工作模式
     */
    private int workType;
    /**
     * 参数类型：10：下单、20查单、30通知、40查余额
     */
    private int paramType;
    /**
     * 三方通道状态
     */
    private int status;
    /**
     * 提单来源类型：测试:10
     */
    private int orderType;

}
