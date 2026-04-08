package com.jdktomcat.showcase.legacy.system.component.httpclient.context;

import lombok.Data;

@Data
public class WithdrawContext extends FastContext {
    /**
     * 提款地址
     */
    private String withdrawUrl;
    /**
     * 三方订单，商户反查token
     */
    private String token;
    /**
     * 金额
     */
    private String amount;
    /**
     * 虚拟币的协议
     */
    private String protocol;
    /**
     * 钱包地址
     */
    private String walletAddress;
    /**
     * 提款人姓名
     */
    private String ownerName;
    /**
     * 银行卡号
     */
    private String bankNumber;


    /**
     * 支付的通知url的key
     */
    private String  notifyUrlKey;
    /**
     * 支付的通知url的参数格式:json/form，默认为空则不走回调，走主动查单逻辑
     */
    private String  notifyUrlType;
}
