package com.jdktomcat.showcase.rocketmq.transaction.consumer.service;

import com.jdktomcat.showcase.rocketmq.transaction.consumer.entry.Order;

public interface IWithdrawOrderService {

    void saveMac(Order order);

}
