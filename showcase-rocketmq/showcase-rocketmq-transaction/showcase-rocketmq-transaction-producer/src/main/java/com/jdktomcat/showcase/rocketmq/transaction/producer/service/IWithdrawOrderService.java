package com.jdktomcat.showcase.rocketmq.transaction.producer.service;

import com.jdktomcat.showcase.rocketmq.transaction.producer.model.ThirdWithdrawOrder;

public interface IWithdrawOrderService {
    ThirdWithdrawOrder createOrder();
}
