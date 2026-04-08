package com.jdktomcat.showcase.legacy.system.component.httpclient.context;

import lombok.Data;

@Data
public class NotifyParamVO {
    /**
     * 响应结果:1:成功  0处理中  -1失败
     */
    private int resultStatus;

    /**
     * 三方订单状态
     */
    private QueryResult data = new QueryResult();

    /**
     * 支付通知的订单ID
     */
    private String orderId;
    /**
     * 支付通知的订单md5 ID
     */
    private String md5OrderId;

    /**
     * 支付通知的三方商户订单ID
     */
    private String channelOrderId;
    /**
     * 订单金额
     */
    private String amount;
    /**
     * 支付通知的三方商户订单实际金额
     */
    private String paidAmount;
    /**
     * 真实付款人姓名
     */
    private String realPayName;
    /**
     * 三方商户通知我方时，返回商户的结果,成功时,默认为：success,可通过:notify_res_success属性进行定制化成功结果
     */
    private String result = "fail";

    /**
     * 当前的金额类型
     */
    private String amountType;

    /**
     * 拆单补贴金额
     */
    private String splitSubsidyAmount;

    /**
     * 虚拟币金额
     */
    private String virtualPaidAmount;
}
