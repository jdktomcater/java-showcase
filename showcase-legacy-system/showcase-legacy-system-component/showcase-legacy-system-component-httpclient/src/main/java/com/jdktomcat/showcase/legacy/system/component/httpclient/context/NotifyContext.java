package com.jdktomcat.showcase.legacy.system.component.httpclient.context;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NotifyContext  extends  FastContext{
    /**
     * 三方查单地址
     */
    private String queryUrl;
    /**
     * 三方订单id
     */
    private String md5OrderId;
    /**
     * 支付渠道id
     */
    private  int  payChannelId;
    /**
     * 充值跳转方式: 1：返卡信息，2：直接跳转商户收银台，3：返二维码，4：H5，5：请求商户后再跳转商户收银台，10：跳转天下收银台
     */
    private int  linkType;
    /**
     *  接收商户通知成功标识符
     */
    private String  notifyReceiveSuccess="success";
    /**
     *  接收商户通知失败标识符
     */
    private String  notifyReceiveFail="fail";

    /**
     * 三方商户发送过来的通知信息
     */
    private  NotifyParamVO   notifyParamVO;

    /**
     * 查单:入参
     */
    private List<ThirdCallParams> queryInParams;
    /**
     * 查单:出参
     */
    private List<ThirdCallParams> queryOutParams;

    /**
     * 通知参数
     */
    private Map notifyData;
    @Override
    public String toString() {
        return "Context{" +
                "merchantCode='" + this.getMerchantCode() + '\'' +
                "channelMerchantId='" + this.getChannelMerchantId() + '\'' +
                ", orderId='" + this.getOrderId() + '\'' +
                ", linkType=" + linkType +
                ", method='" + this.getMethod() + '\'' +
                ", queryUrl='" + queryUrl + '\'' +
                '}';
    }
}
