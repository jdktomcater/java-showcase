package com.jdktomcat.showcase.spring.ai.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;

@Slf4j
@SpringBootApplication
public class SpringAiCsBotApplication implements CommandLineRunner {

    @Autowired
    private AiCsBot aiCsBot;

    public static void main(String[] args) {
        SpringApplication.run(SpringAiCsBotApplication.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        // Ensure long polling can receive updates by removing any existing webhook
        aiCsBot.execute(new DeleteWebhook());
        telegramBotsApi.registerBot(aiCsBot);
        log.info("bot long polling registered successfully!");
    }
}



