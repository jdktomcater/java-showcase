package com.jdktomcat.showcase.spring.ai.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
/**
 * 机器人配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotConfig {

    /**
     * 密钥
     */
    private String token;

    /**
     * 名称
     */
    private String username;

}


