package com.jdktomcat.showcase.prometheus.base.service;

import com.jdktomcat.showcase.prometheus.base.entry.Order;

import java.math.BigDecimal;

public interface OrderService {

    Order create(BigDecimal amount);
}
