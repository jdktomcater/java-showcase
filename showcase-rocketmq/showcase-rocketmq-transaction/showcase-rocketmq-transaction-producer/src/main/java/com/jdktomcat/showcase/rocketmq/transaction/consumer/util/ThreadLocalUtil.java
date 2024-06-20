package com.jdktomcat.showcase.rocketmq.transaction.consumer.util;

import com.jdktomcat.showcase.rocketmq.transaction.consumer.model.WithdrawOrder;

public class ThreadLocalUtil {
    /**
     * 本地线程缓存
     */
    public static final ThreadLocal<WithdrawOrder> WITHDRAW_ORDER_THREAD_LOCAL = new ThreadLocal<>();
}
