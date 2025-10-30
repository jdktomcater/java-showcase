package com.jdktomcat.showcase.spring.ai.bot;

import com.jdktomcat.showcase.spring.ai.bot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * 机器人定制
 */
@Slf4j
@Component
public class AiCsBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;

    public AiCsBot(BotConfig botConfig) {
        super(botConfig.getToken());
        this.botConfig  = botConfig;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("onUpdateReceived:{}", update);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
        log.info("onUpdatesReceived:{}", updates);
    }
}
