package com.jdktomcat.showcase.langchain4j;

import dev.langchain4j.model.openai.OpenAiChatModel;

/**
 * Hello world!
 *
 */
public class HelloWorld {
    public static void main( String[] args ) {
        String apiKey = System.getenv("OPEN_AI_KEY");
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .build();
        String answer = model.chat("Say 'Hello World'");
        // Hello World
        System.out.println(answer);
    }
}
