package com.jdktomcat.showcase.system.design.pay.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompensationJob {

    @Scheduled(fixedDelay = 60000)
    public void compensate() {
        // 查 PROCESSING 超时订单
        // 主动查询渠道状态
        // 修正账务
    }
}

