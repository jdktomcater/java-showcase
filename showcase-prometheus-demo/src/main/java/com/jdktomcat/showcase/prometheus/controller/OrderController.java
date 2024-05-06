package com.jdktomcat.showcase.prometheus.controller;

import com.jdktomcat.showcase.prometheus.entry.Order;
import com.jdktomcat.showcase.prometheus.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;


@RestController
@RequestMapping(value = "/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     *
     * @return 订单编号
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(BigDecimal amount) {
        Order order = orderService.create(amount);
        if(order!=null){
            return order.getNo();
        }
        return "fail";
    }
}
