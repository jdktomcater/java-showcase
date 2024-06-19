package com.jdktomcat.showcase.rocketmq.transaction.producer.service;

import com.jdktomcat.showcase.rocketmq.transaction.producer.model.WithdrawOrder;
import com.jdktomcat.showcase.rocketmq.transaction.producer.web.OrderTO;

public interface IWithdrawOrderService {
    WithdrawOrder createOrder(OrderTO orderTO) throws InterruptedException;
}
