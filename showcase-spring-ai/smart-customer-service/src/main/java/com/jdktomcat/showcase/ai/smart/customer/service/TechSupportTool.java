package com.jdktomcat.showcase.ai.smart.customer.service;

import org.springframework.stereotype.Service;

@Service
public class TechSupportTool {

    public String answer(String question) {
        if (question.contains("支付")) {
            return "请确认您的支付方式支持国际交易，或尝试切换支付宝/信用卡。";
        }
        if (question.contains("登录")) {
            return "请清除浏览器缓存后重试，或尝试重置密码。";
        }
        return "请稍等，我将为您联系技术工程师进一步确认。";
    }
}
