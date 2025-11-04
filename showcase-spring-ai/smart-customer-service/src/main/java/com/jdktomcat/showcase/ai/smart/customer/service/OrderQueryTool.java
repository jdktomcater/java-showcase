package com.jdktomcat.showcase.ai.smart.customer.service;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class OrderQueryTool {
    private static final Map<String, String> MOCK_ORDERS = Map.of(
            "1001", "已发货，预计明天送达",
            "1002", "处理中，预计24小时内出库",
            "1003", "已签收，谢谢您的支持！"
    );

    public String getOrderStatus(String orderId) {
        return MOCK_ORDERS.getOrDefault(orderId, "未找到该订单，请确认订单号是否正确。");
    }
}
