package com.jdktomcat.showcase.rocketmq.transaction.producer.web.controller;

import com.jdktomcat.showcase.rocketmq.transaction.producer.service.IWithdrawOrderService;
import com.jdktomcat.showcase.rocketmq.transaction.producer.web.to.OrderTO;
import com.jdktomcat.showcase.rocketmq.transaction.producer.web.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/withdraw")
public class WithdrawController {

    @Autowired
    private IWithdrawOrderService withdrawOrderService;

    @PostMapping(value = "/create")
    public Result<?> create(OrderTO orderTO) throws InterruptedException {
        //统一返回值
        return Result.success(withdrawOrderService.createOrder(orderTO));
    }
}
