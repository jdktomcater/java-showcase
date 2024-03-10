package com.jdktomcat.demo.limiter.consumer.message;

import lombok.Data;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：告警信息
 * @date 2023/11/27
 */
@Data
public class AlertMessage {
    /**
     * 机器人
     */
    private String bot;

    /**
     * 群组
     */
    private String chat;

    /**
     * 告警信息
     */
    private String message;
}
