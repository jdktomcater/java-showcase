package com.jdktomcat.showcase.spring.ai.bot.service;

import com.jdktomcat.showcase.spring.ai.bot.dto.OrderQueryRequest;
import com.jdktomcat.showcase.spring.ai.bot.util.JsonUtils;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.internal.chat.ResponseFormat;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * AI订单服务接口
 */
@Slf4j
@Service
public class AiOrderService {

    @Value("${deepseek.apiKey}")
    private String apiKey;

    private OpenAiChatModel model;

    @PostConstruct
    public void init() {
        model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("deepseek-chat")
                .build();
    }

    public OrderQueryRequest extractOrderParams(String userMsg) {

        String prompt = """
            从用户消息中提取订单号（merchant_order_id）。
            用户消息：%s
            """.formatted(userMsg);

        String json = model.chat(prompt);
        return JsonUtils.fromJson(json, OrderQueryRequest.class);
    }

}
