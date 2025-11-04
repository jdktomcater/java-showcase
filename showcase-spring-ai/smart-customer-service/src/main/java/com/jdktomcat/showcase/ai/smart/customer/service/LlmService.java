package com.jdktomcat.showcase.ai.smart.customer.service;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LlmService {

    private final OpenAiChatModel model;

    public LlmService(@Value("${deepseek.api-key:}") String apiKey, @Value("${deepseek.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}") String baseUrl, @Value("${deepseek.model:deepseek-r1-0528}") String modelName) {

        this.model = OpenAiChatModel.builder().apiKey(apiKey).baseUrl(baseUrl).modelName(modelName).temperature(0.3).build();
    }

    public String ask(String message) {
        return model.generate(message);
    }
}
