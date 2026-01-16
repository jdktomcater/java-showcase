
package com.jdktomcat.showcase.system.design.pay.web.controller;

import com.jdktomcat.showcase.system.design.pay.web.request.DeductRequest;
import com.jdktomcat.showcase.system.design.pay.web.response.Response;
import com.jdktomcat.showcase.system.design.pay.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pay")
public class PayController {

    private final PaymentService paymentService;

    public PayController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * user deduct end point
     *
     * @param deductRequest deduct request
     * @return response
     */
    @PostMapping("/deduct")
    public Response<String> deduct(@RequestBody DeductRequest deductRequest) {
        // param validate
        return Response.success(paymentService.pay(deductRequest.getRequestId(), deductRequest.getUserId(), deductRequest.getAmount()));
    }
}
