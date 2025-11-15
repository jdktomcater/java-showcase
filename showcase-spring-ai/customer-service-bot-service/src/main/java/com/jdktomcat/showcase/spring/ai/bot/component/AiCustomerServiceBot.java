package com.jdktomcat.showcase.spring.ai.bot.component;

import com.jdktomcat.showcase.spring.ai.bot.config.TelegramBotConfig;
import com.jdktomcat.showcase.spring.ai.bot.dto.OrderDTO;
import com.jdktomcat.showcase.spring.ai.bot.dto.OrderQueryRequest;
import com.jdktomcat.showcase.spring.ai.bot.service.AiOrderService;
import com.jdktomcat.showcase.spring.ai.bot.service.AiReplyService;
import com.jdktomcat.showcase.spring.ai.bot.service.OrderQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
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

    /**
     * AI订单服务
     */
    private final AiOrderService aiOrderService;

    /**
     * AI相应服务
     */
    private final AiReplyService aiReplyService;

    /**
     * 订单查询服务
     */
    private final OrderQueryService orderQueryService;


    /**
     * 构造器
     *
     * @param telegramBotConfig 配置
     * @param aiOrderService    服务
     * @param aiReplyService    服务
     * @param orderQueryService 服务
     */
    public AiCustomerServiceBot(TelegramBotConfig telegramBotConfig, AiOrderService aiOrderService, AiReplyService aiReplyService, OrderQueryService orderQueryService) {
        super(telegramBotConfig.getToken());
        this.telegramBotConfig = telegramBotConfig;
        this.aiOrderService = aiOrderService;
        this.aiReplyService = aiReplyService;
        this.orderQueryService = orderQueryService;
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
        if (!update.hasMessage() || !update.getMessage().hasText()) return;
        Message msg = update.getMessage();
        String text = msg.getText();
        Chat chat = msg.getChat();
        try {
            // 1. 调用 DeepSeek 解析订单参数
            OrderQueryRequest params = aiOrderService.extractOrderParams(text);
            if (params.getMerchant_order_id() == null) {
                send(chat.getId(), "请提供正确的订单号，例如：查询订单号 123456",msg.getMessageId());
                return;
            }
            // 2. 查询订单
            OrderDTO order = orderQueryService.query(params.getMerchant_order_id());
            if (order == null) {
                send(chat.getId(), "未找到该订单，请确认订单号是否正确。",msg.getMessageId());
                return;
            }
            // 3. AI 生成客服回答
            String reply = aiReplyService.buildReply(order);
            send(chat.getId(), reply,msg.getMessageId());
        } catch (Exception e) {
            send(chat.getId(), "系统开小差了，请稍后再试 🫠",msg.getMessageId());
        }
    }

    private void send(long chatId, String text, Integer messageId) {
        try {
            SendMessage msg = new SendMessage(String.valueOf(chatId), text);
            msg.setReplyToMessageId(messageId);
            execute(msg);
        } catch (Exception ignored) {}
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
