package com.jdktomcat.showcase.prometheus.service.impl;

import com.alibaba.fastjson2.JSON;
import com.jdktomcat.showcase.prometheus.entry.Order;
import com.jdktomcat.showcase.prometheus.service.OrderService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MeterRegistry registry;


    // 累计成功订单数量
    private Counter successCounter;

    // 累计失败订单数量
    private Counter failedCounter;

    // 成功订单金额
    private DistributionSummary successSummary;

    // 失败订单金额
    private DistributionSummary failedSummary;


    @PostConstruct
    private void init() {
        successCounter = registry.counter("create_order_total", "create_order_total", "success");
        failedCounter = registry.counter("create_order_total", "create_order_total", "failed");
        successSummary = registry.summary("order_amount", "order_amount", "success");
        failedSummary = registry.summary("order_amount", "order_amount", "failed");
    }


    // 创建订单

    @Override
    public Order create(BigDecimal amount) {
        log.info("createOrder orderAmount:{}", amount);
        Order order = null;
        try {
            if (amount.compareTo(BigDecimal.valueOf(10)) < 0) {
                throw new Exception("金额小于10时，模拟订单失败的情况");
            }
            order = new Order();
            order.setAmount(amount);
            order.setNo(RandomStringUtils.randomAlphabetic(32));
            order.setCreate(System.currentTimeMillis());
            // 订单成功数量埋点数据
            successCounter.increment();
            // 订单成功金额埋点数据
            successSummary.record(amount.doubleValue());
        } catch (Exception e) {
            log.info("createOrder error", e);
            // 订单失败数量埋点数据
            failedCounter.increment();
            // 订单失败金额埋点数据
            failedSummary.record(amount.doubleValue());
            order = null;
        } finally {
            log.info("queryOrder result:{}", JSON.toJSONString(order));
        }
        return order;
    }
}
