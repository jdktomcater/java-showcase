package com.jdktomcat.demo.rocketmq.consumer.constant;

import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class RocketMQConstant {

    /**
     * 延迟消息队列主题
     */
    public static final String DELAY_MESSAGE_TOPIC = "DELAY_MESSAGE_TOPIC_NEW";
    /**
     * 延迟消息队列主题
     */
    public static final String DELAY_MESSAGE_TOPIC_TAG_VOUCHER = "voucher";

    /**
     * 延迟循环tag标记
     */
    public static final String DELAY_CYCLE_PREFIX = "cycle_";

    /**
     * 延迟消息级别枚举
     */
    @Getter
    public enum DelayLevelEnum {

        DELAY_1S(1,1, "1秒"),

        DELAY_5S(2,5, "5秒"),

        DELAY_10S(3,10, "10秒"),

        DELAY_30S(4,30, "30秒"),

        DELAY_1m(5,60, "1分钟"),

        DELAY_2m(6,120, "2分钟"),

        DELAY_3m(7,180, "3分钟"),

        DELAY_4m(8,240, "4分钟"),

        DELAY_5m(9,300, "5分钟"),

        DELAY_6m(10,360, "6分钟"),

        DELAY_7m(11,420, "7分钟"),

        DELAY_8m(12,480, "8分钟"),

        DELAY_9m(13,540, "9分钟"),

        DELAY_10m(14,600, "10分钟"),

        DELAY_20m(15,1200, "20分钟"),

        DELAY_30m(16,1800, "30分钟"),

        DELAY_1h(17,3600, "1小时"),

        DELAY_2h(18,7200, "2小时");

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

    /**
     * 延迟消息包装类
     */
    @Data
    public static class DelayMessageWrapper {
        /**
         * 总时间
         */
        private int totalTimeout;

        /**
         * 剩余时间
         */
        private int expireTimeout;

        /**
         * 包装数据
         */
        private String data;

        public DelayMessageWrapper(int totalTimeout, int expireTimeout, String data) {
            this.totalTimeout = totalTimeout;
            this.expireTimeout = expireTimeout;
            this.data = data;
        }
    }

    /**
     * 全部topic初始化到map中供config中注册消费者 key--topic  value--topic下的所有tag
     * 自定义测试新增tag添加到如下任意一个topic value中即可
     * @return 全部topic：tag
     */
    public static Map<String,String[]> fetchTopicArray(){
        Map<String,String []> TOPIC_AND_TAG = new HashMap<>();
        // 延迟消息订阅TAG
        TOPIC_AND_TAG.put(RocketMQConstant.DELAY_MESSAGE_TOPIC, new String[]{
                        RocketMQConstant.DELAY_CYCLE_PREFIX + RocketMQConstant.DELAY_MESSAGE_TOPIC_TAG_VOUCHER,
                        RocketMQConstant.DELAY_MESSAGE_TOPIC_TAG_VOUCHER,
                }
        );
        return TOPIC_AND_TAG;
    }
}
