package com.jdktomcat.showcase.ai.smart.customer.service.prototype.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatService {
    private final OpenAiClient openAiClient;
    private final MilvusClientWrapper milvusClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatService(OpenAiClient openAiClient, MilvusClientWrapper milvusClient) {
        this.openAiClient = openAiClient;
        this.milvusClient = milvusClient;
    }

    public Map<String, Object> handle(String user, String message, String orderId) {
        boolean isOrderQuery = message != null && message.matches(".*\\b(订单|order)\\b.*");

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("input", message);

        if(isOrderQuery && orderId != null){
            Map<String,Object> orderInfo = Map.of("orderId", orderId, "status","PROCESSING", "channelCode","E302");
            result.put("orderInfo", orderInfo);
        }

        List<String> docs = milvusClient.searchSimilar(message, 3);
        result.put("retrieved", docs);

        String prompt = "根据订单信息和知识库片段给出回答。\n问题:" + message + "\n知识片段:" + docs.toString();
        String answer = openAiClient.generateAnswer(prompt);

        result.put("answer", answer);
        return result;
    }
}
