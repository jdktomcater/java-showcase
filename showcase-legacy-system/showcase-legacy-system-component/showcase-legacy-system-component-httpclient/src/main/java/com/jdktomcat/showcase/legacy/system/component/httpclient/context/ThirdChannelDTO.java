package com.jdktomcat.showcase.legacy.system.component.httpclient.context;

import lombok.Data;

@Data
public class ThirdChannelDTO {
    /**
     * 三方商户ID
     */
    private int merchantId;
    /**
     * 三方商户编码
     */
    private String merchantCode;
    /**
     * 三方商户渠道号
     */
    private String channelMerchantId;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 充值URL
     */
    private String depositUrl;

    /**
     * 提款URL
     */
    private String withdrawUrl;

    /**
     * 充值跳转方式
     */
    private int linkType;

    /**
     * 工作模式
     */
    private int workType;
    /**
     * 密钥KEY名称
     */
    private String privateKeyName;
    /**
     * 提款查单地址
     */
    private String  withdrawQueryOrderUrl;
    /**
     * 充值查单地址
     */
    private String  depositQueryOrderUrl;
    /**
     * 余额查询地址
     */
    private String  balanceQueryUrl;
    /**
     * 反查开关：0 不开启（默认）1 开启
     */
    private int callStatus=1;
    /**
     * 私钥加密
     */
    private String  privateKey="";
    /**
     * 公钥解密
     */
    private String  publicKey="";

    @Override
    public String toString() {
        return "ThirdChannelDTO{" +
                "merchantId=" + merchantId +
                ", merchantCode='" + merchantCode + '\'' +
                ", channelMerchantId='" + channelMerchantId + '\'' +
                ", payType='" + payType + '\'' +
                ", depositUrl='" + depositUrl + '\'' +
                ", withdrawUrl='" + withdrawUrl + '\'' +
                ", linkType=" + linkType +
                ", workType=" + workType +
                ", privateKeyName='" + privateKeyName + '\'' +
                ", withdrawQueryOrderUrl='" + withdrawQueryOrderUrl + '\'' +
                ", depositQueryOrderUrl='" + depositQueryOrderUrl + '\'' +
                ", balanceQueryUrl='" + balanceQueryUrl + '\'' +
                ", callStatus=" + callStatus +
                '}';
    }
}