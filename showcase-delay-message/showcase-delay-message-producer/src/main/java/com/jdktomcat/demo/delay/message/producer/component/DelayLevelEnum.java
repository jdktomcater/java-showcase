package com.jdktomcat.demo.delay.message.producer.component;

import lombok.Getter;

/**
 * 延迟消息级别枚举
 */
@Getter
public enum DelayLevelEnum {
    DELAY_1S(1, 1, "1秒"),

    DELAY_5S(2, 5, "5秒"),

    DELAY_10S(3, 10, "10秒"),

    DELAY_30S(4, 30, "30秒"),

    DELAY_1m(5, 60, "1分钟"),

    DELAY_2m(6, 120, "2分钟"),

    DELAY_3m(7, 180, "3分钟"),

    DELAY_4m(8, 240, "4分钟"),

    DELAY_5m(9, 300, "5分钟"),

    DELAY_6m(10, 360, "6分钟"),

    DELAY_7m(11, 420, "7分钟"),

    DELAY_8m(12, 480, "8分钟"),

    DELAY_9m(13, 540, "9分钟"),

    DELAY_10m(14, 600, "10分钟"),

    DELAY_20m(15, 1200, "20分钟"),

    DELAY_30m(16, 1800, "30分钟"),

    DELAY_1h(17, 3600, "1小时"),

    DELAY_2h(18, 7200, "2小时");

    /**
     * 延迟级别
     */
    private final int level;

    /**
     * 秒数
     */
    private final int seconds;

    /**
     * 描绘信息
     */
    private final String desc;

    DelayLevelEnum(int level, int seconds, String desc) {
        this.level = level;
        this.seconds = seconds;
        this.desc = desc;
    }

    /**
     * 查找最近延迟级别
     *
     * @param timeout 超时时长
     * @return 延迟级别
     */
    public static DelayLevelEnum findNearbyLevel(int timeout) {
        if (timeout < 0) {
            throw new RuntimeException("查找最近延迟级别参数无效:" + timeout);
        }
        DelayLevelEnum[] levelEnums = DelayLevelEnum.values();
        for (int length = levelEnums.length; length > 0; length--) {
            DelayLevelEnum delayLevelEnum = levelEnums[length - 1];
            if (delayLevelEnum.getSeconds() <= timeout) {
                return delayLevelEnum;
            }
        }
        throw new RuntimeException("查找最近延迟级别参数无效:" + timeout);
    }
}
