package com.jdktomcat.showcase.mq.rocketmq.transaction.producer.service;

import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.model.WithdrawOrder;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.web.to.OrderTO;

public interface IWithdrawOrderService {
    /**
     * 创建订单
     * @param orderTO 订单信息
     * @return 订单信息
     * @throws InterruptedException 并发异常
     */
    WithdrawOrder createOrder(OrderTO orderTO) throws InterruptedException;

    /**
     * 無鎖創建訂單
     *
     * @param orderTO 訂單信息
     * @return 訂單信息
     */
    WithdrawOrder createOrderWithoutLock(OrderTO orderTO);
}
