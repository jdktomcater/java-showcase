package com.jdktomcat.showcase.prometheus.service;

import com.jdktomcat.showcase.prometheus.entry.Order;

import java.math.BigDecimal;

public interface OrderService {

    Order create(BigDecimal amount);
}
