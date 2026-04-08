package com.jdktomcat.showcase.legacy.system.component.httpclient.context;

import lombok.Data;

@Data
public class ThirdCallParams {
    /**
     * 三方商户编码
     */
    private String merchantCode;

    /**
     * 参数类型：10：下单、20查单、30通知、40支付页面参数、50查余额
     */
    private int paramType;
    /**
     *  参数变量名称
     */
    private String paramEnName;

    /**
     *  三方参数值表达式：string/int/double/exp
     */
    private String paramValueExp;

    /**
     *  调用成功响应码：多个响应码用逗号分割
     */
    private String successResValue;
    /**
     *  调用失败响应码：多个响应码用逗号分割
     */
    private String failResValue;
    /**
     *  渠道工作方式
     */
    private int workType;
    /**
     *  加密排序号
     */
    private int orderNo;
}
