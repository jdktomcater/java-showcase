package com.jdktomcat.showcase.ai.smart.customer.service.prototype.prompt;

import lombok.Data;

/**
 * 提示词
 */
@Data
public class OpenAiPrompt {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 消息
     */
    private Message[] messages;

    /**
     * 消息类
     */
    @Data
    public static class Message {

        /**
         * 角色
         */
        private String role;

        /**
         * 内容
         */
        private String content;
    }

}
