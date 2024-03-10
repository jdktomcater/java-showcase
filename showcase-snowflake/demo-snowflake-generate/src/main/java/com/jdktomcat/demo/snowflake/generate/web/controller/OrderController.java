package com.jdktomcat.demo.snowflake.generate.web.controller;

import com.jdktomcat.demo.snowflake.generate.service.IWithdrawOrderService;
import com.jdktomcat.demo.snowflake.generate.web.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IWithdrawOrderService withdrawOrderService;

    @GetMapping("/insert")
    public Result<?> insert() {
        //统一返回值
        try {
            return Result.success(withdrawOrderService.insert());
        } catch (Exception exception) {
            return Result.failed(exception.getMessage());
        }

    }
}