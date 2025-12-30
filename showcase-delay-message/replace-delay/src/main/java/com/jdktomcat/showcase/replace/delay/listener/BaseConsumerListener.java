package com.jdktomcat.showcase.replace.delay.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Slf4j
public abstract class BaseConsumerListener implements MessageListenerConcurrently {

    @Value("${rocketmq.retry:4}")
    private int retry;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        long start = System.currentTimeMillis();
        MessageExt messageExt = list.get(0);
        String tag = messageExt.getTags();
        String topic = messageExt.getTopic();
        log.info("topic:{}, tags:{}, keys:{}, msgId:{}", topic, tag, messageExt.getKeys(), messageExt.getMsgId());
        try {
            // 检查相关参数
            if(StringUtils.isEmpty(tag) || StringUtils.isEmpty(topic)){
                log.error("消息tag or topic为空，消息异常！");
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            consume(tag, topic, messageExt);
            log.info("-----------------------mq消费consumeMessage耗时：{}", System.currentTimeMillis() - start);
        } catch (Throwable e) {
            log.error("consumeMessage occur some exception!", e);
            return canRetry(messageExt);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * {@link BaseConsumerListener()}方法被多个子类重写过，使用此方法替代原有方法获取重试次数
     *
     * @return 重试次数
     */
    protected int getRetryCount() {
        return retry;
    }


    /**
     * 消息重试  aleck for add  2019-12-14
     *
     * @param messageExt 消息体
     * @return 返回重试状态
     */
    protected ConsumeConcurrentlyStatus canRetry(MessageExt messageExt) {
        if (messageExt.getReconsumeTimes() < getRetryCount()) {
            log.warn("topic:{},tag:{} 消费失败最大重试{}次, 重试第{}次 ", messageExt.getTopic(), messageExt.getTags(), getRetryCount(), messageExt.getReconsumeTimes() + 1);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        } else {
            log.error("topic:{},tag:{} 消费失败重试次数：{} 已经最大,发出告警 ", messageExt.getTopic(), messageExt.getTags(), messageExt.getReconsumeTimes());
            try {
                //telegram消息推送
                JSONObject jSONObject = JSONObject.parseObject(new String(messageExt.getBody()));
                String orderId = jSONObject.getString("OrderID");
                String consumeWarnMsg = String.format("消息处理失败，请手动处理这条超时订单数据:msgId: %s, keys: %s, orderId: %s", messageExt.getMsgId(), messageExt.getKeys(), orderId);
                log.warn(consumeWarnMsg);
                log.info("发送纸飞机telegram消息完成");
            } catch (Exception exception) {
                log.info("消息处理失败，发送纸飞机telegram消息异常！！！");
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    protected abstract void consume(String tag, String topic, MessageExt messageExt);

    /**
     * 格式化显示消息
     *
     * @param messageExt 消息
     * @return 格式化字符串
     */
    protected String toString(MessageExt messageExt) {
        return "MessageExt [" + "topic='" + messageExt.getTopic() + ", flag=" + messageExt.getFlag() + ", properties=" + messageExt.getProperties() + ", transactionId='" + messageExt.getTransactionId() + "queueId=" + messageExt.getQueueId() + ", storeSize=" + messageExt.getStoreSize() + ", queueOffset=" + messageExt.getQueueOffset() + ", sysFlag=" + messageExt.getSysFlag() + ", bornTimestamp=" + messageExt.getBornTimestamp() + ", bornHost=" + messageExt.getBornHost() + ", storeTimestamp=" + messageExt.getStoreTimestamp() + ", storeHost=" + messageExt.getStoreHost() + ", msgId=" + messageExt.getMsgId() + ", commitLogOffset=" + messageExt.getCommitLogOffset() + ", bodyCRC=" + messageExt.getBodyCRC() + ", reconsumeTimes=" + messageExt.getReconsumeTimes() + ", preparedTransactionOffset=" + messageExt.getPreparedTransactionOffset() + "]";
    }

    /**
     * 订阅tag
     *
     * @return tag组合
     */
    public abstract String tags();

    /**
     * 订阅主题
     *
     * @return 主题
     */
    public abstract String topic();

}
