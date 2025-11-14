package com.jdktomcat.showcase.spring.ai.bot.component;

import com.jdktomcat.showcase.spring.ai.bot.config.TelegramBotConfig;
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
public class AiCustomerServiceBot extends TelegramLongPollingBot {

    /**
     * 机器人配置
     */
    private final TelegramBotConfig telegramBotConfig;

    public AiCustomerServiceBot(TelegramBotConfig telegramBotConfig) {
        super(telegramBotConfig.getToken());
        this.telegramBotConfig = telegramBotConfig;
    }

    @Override
    public String getBotUsername() {
        return telegramBotConfig.getUsername();
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
