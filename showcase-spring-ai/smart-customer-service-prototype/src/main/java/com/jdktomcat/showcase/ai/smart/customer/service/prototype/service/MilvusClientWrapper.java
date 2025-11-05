package com.jdktomcat.showcase.ai.smart.customer.service.prototype.service;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class MilvusClientWrapper {
    private final MilvusServiceClient client;
    private final String collectionName = "faq_orders";

    public MilvusClientWrapper() {
        ConnectParam connectParam = ConnectParam.newBuilder().withHost("localhost").withPort(19530).build();
        this.client = new MilvusServiceClient(connectParam);
    }

    public List<String> searchSimilar(String text, int topK) {
        try {
            List<String> res = new ArrayList<>();
            res.add("FAQ: 下单成功但未回调 -> 检查回调日志和MQ消费");
            res.add("FAQ: 订单卡在处理中 -> 检查渠道回执与对账");
            return res;
        } catch (Exception e) {
            return Collections.singletonList("[Milvus search error] " + e.getMessage());
        }
    }
}
