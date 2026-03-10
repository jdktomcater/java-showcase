package com.jdktomcat.showcase.json.entry;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawOrders {

    /**
     * 商户编码
     */
    private String merchant_ids;
    
    /**
     * 随机token
     */
    private String signs;

    /**
     * 请求类型 1 确认 2 取消 3 优先匹配 4 最优匹配
     */
    private int request_type;

    /**
     * 商户订单ID
     */
    private String merchant_order_ids;

    /**
     * 支付平台订单ID
     */
    private String typay_order_ids;


    /**
     * 实际到账金额  pay_paid_amt
     */
    private BigDecimal pay_paid_amt;
}
