package com.jdktomcat.showcase.legacy.system.component.httpclient.context;

import com.jdktomcat.showcase.legacy.system.component.httpclient.constant.FastConstants;
import lombok.Data;

@Data
public class DepositContext extends  FastContext {
    /**
     * 充值支付地址
     */
    private String depositUrl;
    /**
     * 充值跳转方式: 1：返卡信息，2：直接跳转商户收银台，3：返二维码，4：H5，5：请求商户后再跳转商户收银台，10：跳转天下收银台
     */
    private int  linkType;
    /**
     * true:直接跳转商户支付页面 false:请求商户获取卡信息或者充值URL
     */
    private boolean  directRedirect;
//    /**
//     *  true:跳转支付页面，false：返回卡或二维码信息，
//     */
//    private boolean  payPage;

    /**
     * 支付的通知url的key
     */
    private String  notifyUrlKey="notify_url";
    /**
     * 支付的通知url的参数格式:json/form
     */
    private String  notifyUrlType="body";
    /**
     * 提单来源类型：测试:10
     */
    private int orderType;
    /**
     * 充值页面请求方式: post/get
     */
    private String  depositPageMethod= FastConstants.METHOD_POST;
    @Override
    public String toString() {
        return "DepositContext{" +
                "merchantCode='" + this.getMerchantCode() + '\'' +
                "channelMerchantId='" + this.getChannelMerchantId() + '\'' +
                ", orderId='" + this.getOrderId() + '\'' +
                ", linkType=" + linkType +
                ", method='" + this.getMethod() + '\'' +
                ", depositUrl='" + depositUrl + '\'' +
                ", orderType='" + orderType + '\'' +
                '}';
    }
}
