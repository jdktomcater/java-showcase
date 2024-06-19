package com.jdktomcat.showcase.rocketmq.transaction.producer.web.controller;

import com.jdktomcat.showcase.rocketmq.transaction.producer.service.IWithdrawOrderService;
import com.jdktomcat.showcase.rocketmq.transaction.producer.web.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/withdraw")
public class WithdrawController {

    @Autowired
    private IWithdrawOrderService withdrawOrderService;

    @GetMapping("/one")
    public Result<?> one(){
        //统一返回值
        try {
            return Result.success(withdrawOrderService.actionOne());
        }catch (Exception exception){
            return Result.failed(exception.getMessage());
        }

    }

    @GetMapping("/two")
    public Result<?> two(){
        //统一返回值
        return Result.success(withdrawOrderService.actionTwo());
    }

}
