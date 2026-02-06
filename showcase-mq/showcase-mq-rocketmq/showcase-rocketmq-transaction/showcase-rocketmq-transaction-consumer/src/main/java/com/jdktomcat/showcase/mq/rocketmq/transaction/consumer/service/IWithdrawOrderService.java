package com.jdktomcat.showcase.mq.rocketmq.transaction.consumer.service;

import com.jdktomcat.showcase.mq.rocketmq.transaction.consumer.entry.Order;

public interface IWithdrawOrderService {

    void saveMac(Order order);

}
