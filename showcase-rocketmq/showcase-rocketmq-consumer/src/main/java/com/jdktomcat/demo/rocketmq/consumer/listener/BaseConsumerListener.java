package com.jdktomcat.demo.rocketmq.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

@Slf4j
public abstract class BaseConsumerListener implements MessageListenerConcurrently {


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        long start = System.currentTimeMillis();
        MessageExt messageExt = list.get(0);
        String tag = messageExt.getTags();
        String topic = messageExt.getTopic();
        log.info("topic:{}, tags:{}, keys:{}, msgId:{}", topic, tag, messageExt.getKeys(), messageExt.getMsgId());
        try {
            consume(tag, topic, messageExt);
            log.info("-----------------------mq消费consumeMessage耗时：{}", System.currentTimeMillis() - start);
        } catch (Throwable e) {
            log.error("topic:{},tag:{} 消费失败最大重试{}次, 重试第{}次 ", topic, tag, 4, messageExt.getReconsumeTimes(), e);
            return canRetry(messageExt);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 消息重试  aleck for add  2019-12-14
     *
     * @param messageExt 消息体
     * @return 返回重试状态
     */
    private ConsumeConcurrentlyStatus canRetry(MessageExt messageExt) {
        if (messageExt.getReconsumeTimes() < 4) {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        } else {
            try {
                //telegram消息推送
                JSONObject jSONObject = JSONObject.parseObject(new String(messageExt.getBody()));
                String orderId = jSONObject.getString("OrderIndex");
                String consumeWarnMsg = String.format("消息处理失败，请手动处理这条超时订单数据:msgId: %s, keys: %s, orderId: %s", messageExt.getMsgId(), messageExt.getKeys(), orderId);
                log.warn(consumeWarnMsg);
            } catch (Exception exception) {
                log.info("消息处理失败，发送纸飞机telegram消息异常！！！");
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    protected abstract void consume(String tag, String topic, MessageExt messageExt);

    /**
     * 去掉body打印
     *
     * @param messageExt
     * @return
     */
    protected String toString(MessageExt messageExt) {
        return "MessageExt [" + "topic='" + messageExt.getTopic() + ", flag=" + messageExt.getFlag() + ", properties=" + messageExt.getProperties() + ", transactionId='" + messageExt.getTransactionId() + "queueId=" + messageExt.getQueueId() + ", storeSize=" + messageExt.getStoreSize() + ", queueOffset=" + messageExt.getQueueOffset() + ", sysFlag=" + messageExt.getSysFlag() + ", bornTimestamp=" + messageExt.getBornTimestamp() + ", bornHost=" + messageExt.getBornHost() + ", storeTimestamp=" + messageExt.getStoreTimestamp() + ", storeHost=" + messageExt.getStoreHost() + ", msgId=" + messageExt.getMsgId() + ", commitLogOffset=" + messageExt.getCommitLogOffset() + ", bodyCRC=" + messageExt.getBodyCRC() + ", reconsumeTimes=" + messageExt.getReconsumeTimes() + ", preparedTransactionOffset=" + messageExt.getPreparedTransactionOffset() + "]";
    }

}
