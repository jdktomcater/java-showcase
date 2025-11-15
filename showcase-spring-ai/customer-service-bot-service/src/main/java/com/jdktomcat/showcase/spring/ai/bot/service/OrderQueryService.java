package com.jdktomcat.showcase.spring.ai.bot.service;

import com.jdktomcat.showcase.spring.ai.bot.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 订单查询服务接口
 */
@Slf4j
@Service
public class OrderQueryService {

    @Value("${order.api}")
    private String orderApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public OrderDTO query(String merchantOrderId) {
        try {
            return restTemplate.getForObject(orderApiUrl + merchantOrderId, OrderDTO.class);
        } catch (Exception e) {
            return null;
        }
    }
}
