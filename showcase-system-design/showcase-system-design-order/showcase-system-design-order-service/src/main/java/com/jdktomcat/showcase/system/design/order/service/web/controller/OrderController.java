
package com.jdktomcat.showcase.system.design.order.service.web.controller;

import com.jdktomcat.showcase.system.design.common.web.response.Response;
import com.jdktomcat.showcase.system.design.order.service.service.PaymentService;
import com.jdktomcat.showcase.system.design.order.service.web.request.DeductRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final PaymentService paymentService;

    public OrderController(PaymentService paymentService) {
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
