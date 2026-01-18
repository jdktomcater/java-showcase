
package com.jdktomcat.showcase.system.design.pay.service.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompensationJob {

    @Scheduled(fixedDelay = 60000)
    public void compensate() {
        // scan processing orders and compensate
    }
}
