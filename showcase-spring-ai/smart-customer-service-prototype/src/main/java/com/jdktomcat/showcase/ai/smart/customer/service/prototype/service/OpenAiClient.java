package com.jdktomcat.showcase.ai.smart.customer.service.prototype.service;

import com.alibaba.fastjson2.JSON;
import com.jdktomcat.showcase.ai.smart.customer.service.prototype.prompt.OpenAiPrompt;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OpenAiClient {

    private final WebClient webClient;

    private final String apiKey;

    public OpenAiClient(@Value("${openai.api.key:}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder().baseUrl("https://api.openai.com/v1").build();
    }

    public String generateAnswer(String prompt){
        if(apiKey == null || apiKey.isEmpty()){
            return "[Mock Answer] 模拟回答：请在配置中填写 OPENAI_API_KEY 来启用真实生成。";
        }
        try{
            OpenAiPrompt openAiPrompt = new OpenAiPrompt();
            openAiPrompt.setModel("gpt-4o-mini");
            OpenAiPrompt.Message message = new OpenAiPrompt.Message();
            message.setRole("user");
            message.setContent(prompt);
            OpenAiPrompt.Message[] messages = new OpenAiPrompt.Message[]{message};
            openAiPrompt.setMessages(messages);
            String body = JSON.toJSONString(openAiPrompt);
            String resp = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization","Bearer "+apiKey)
                    .header("Content-Type","application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return resp == null ? "" : resp;
        }catch(Exception e){
            return "[Error calling OpenAI] " + e.getMessage();
        }
    }
}
