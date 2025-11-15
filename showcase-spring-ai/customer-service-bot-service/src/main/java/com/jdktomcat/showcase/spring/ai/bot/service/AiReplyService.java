package com.jdktomcat.showcase.spring.ai.bot.service;

import com.jdktomcat.showcase.spring.ai.bot.dto.OrderDTO;
import com.jdktomcat.showcase.spring.ai.bot.util.JsonUtils;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiReplyService {

    @Value("${deepseek.apiKey}")
    private String apiKey;

    private OpenAiChatModel model;

    @PostConstruct
    public void init() {
        model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("deepseek-chat")
                .temperature(0.1)
                .build();
    }

    public String buildReply(OrderDTO order) {

        String prompt = """
            请根据以下订单 JSON，生成专业、清晰、简洁的客服回复。
            JSON：%s

            需要包含：
            - 订单状态（解释状态含义）
            - 应付金额、已付金额
            - 用户姓名
            - 支付方式、银行
            - 是否成功
            """.formatted(JsonUtils.toJson(order));

        return model.chat(prompt);
    }
}
