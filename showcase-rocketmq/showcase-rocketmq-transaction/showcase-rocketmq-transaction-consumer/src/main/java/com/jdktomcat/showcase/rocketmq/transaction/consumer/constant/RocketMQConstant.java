package com.jdktomcat.showcase.rocketmq.transaction.consumer.constant;

import java.util.HashMap;
import java.util.Map;

public class RocketMQConstant {

    public static final String MAC_TRANSACTION_TOPIC = "transaction_message_topic";

    public static final String MAC_TRANSACTION_TOPIC_TAG = "transaction_message_topic_tag";

    /**
     * 全部topic初始化到map中供config中注册消费者 key--topic  value--topic下的所有tag
     * 自定义测试新增tag添加到如下任意一个topic value中即可
     * @return 全部topic：tag
     */
    public static Map<String,String[]> fetchTopicArray(){
        Map<String,String []> TOPIC_AND_TAG = new HashMap<>();
        // 延迟消息订阅TAG
        TOPIC_AND_TAG.put(RocketMQConstant.MAC_TRANSACTION_TOPIC, new String[]{
                        RocketMQConstant.MAC_TRANSACTION_TOPIC_TAG,
                }
        );
        return TOPIC_AND_TAG;
    }
}
