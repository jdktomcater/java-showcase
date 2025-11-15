package com.jdktomcat.showcase.spring.ai.bot.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private long merchant_id;
    private String merchant_order_id;
    private String typay_order_id;
    private int pay_type;
    private double pay_amt;
    private double pay_paid_amt;
    private int pay_message;
    private String remark;
    private String sign;
    private String bank_code;
    private String match_merchant_name;
    private String match_merchant_order_id;
    private String transfer_account;
    private String deposit_account;
    private String member_account;
    private int pay_channel_index;
    private String user_paid_name;
    private String operator;
    private String finance_remark;
    private double paid_amount;
    private int order_status;
    private long pay_time;
    private long complete_time;
    private int is_fast_pay;
    private String deposit_paid_name;
}
