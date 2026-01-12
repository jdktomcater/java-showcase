
package com.jdktomcat.showcase.system.design.pay.controller;

import com.jdktomcat.showcase.system.design.pay.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pay")
public class PayController {

    private final PaymentService paymentService;

    public PayController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/deduct")
    public String deduct(@RequestParam String requestId,
                         @RequestParam Long userId,
                         @RequestParam Long amount) {
        paymentService.pay(requestId, userId, amount);
        return "OK";
    }
}
