package com.jdktomcat.showcase.spring.ai.bot.component;

import com.jdktomcat.showcase.spring.ai.bot.config.TelegramBotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        if (!update.hasMessage()) return;
        Message msg = update.getMessage();
        String text = msg.getText();
        Chat chat = msg.getChat();
        String botUsername = getBotUsername();
        // 群聊 @机器人
        if (chat.isGroupChat() || chat.isSuperGroupChat()) {
            if (text != null && text.contains("@" + botUsername)) {
                String cleanText = text.replace("@" + botUsername, "").trim();
                sendText(chat.getId(), "你在群里@我了，我听到了：" + cleanText, msg.getMessageId());
            }
            return;
        }
        // 私聊
        if (chat.isUserChat()) {
            sendText(chat.getId(), "私聊收到：" + text, msg.getMessageId());
        }
    }

    public void sendText(Long chatId, String text, Integer messageId) {
        SendMessage sm = new SendMessage(chatId.toString(), text);
        sm.setReplyToMessageId(messageId);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
        log.info("onUpdatesReceived:{}", updates);

    }
}
