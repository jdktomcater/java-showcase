package com.jdktomcat.showcase.ai.smart.customer.service;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatMemoryService {

    private final ConversationalChain chain;

    public ChatMemoryService(LlmService llmService) {
        this.chain = ConversationalChain.builder()
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
//                .chatLanguageModel()
                .build();
    }

    public String chat(String input) {
        return chain.execute(input);
    }
}
