package com.jdktomcat.showcase.replace.delay.component;

import com.alibaba.fastjson.JSONObject;
import com.jdktomcat.showcase.replace.delay.enums.DelayLevelEnum;
import com.jdktomcat.showcase.replace.delay.wrapper.DelayMessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static com.jdktomcat.showcase.replace.delay.constant.ReplaceDelayConstant.DELAY_CYCLE_PREFIX;
import static com.jdktomcat.showcase.replace.delay.constant.ReplaceDelayConstant.DELAY_MESSAGE_TOPIC;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：消息生产者服务组件
 * @date 2023/11/8
 */
@Slf4j
@Component
public class MessageProducerComponent {

    @Autowired
    private TransactionMQProducer transactionMQProducer;

    /**
     * 重试最大次数配置
     */
    @Value("${rocketmq.producer.retry:10}")
    private int retry;

    /**
     * 异步发送延迟消息
     *
     * @param tag          标签
     * @param key          键
     * @param data         消息体
     * @param delaySeconds 延迟时间（秒）
     */
    public void sendDelayMessage(String tag, String key, String data, int delaySeconds) {
        log.info("发送延时消息,tag:{} key:{} data:{} delaySeconds:{}", tag, key, data, delaySeconds);
        if (delaySeconds > 0) {
            DelayLevelEnum delayLevelEnum = DelayLevelEnum.findNearbyLevel(delaySeconds);
            DelayMessageWrapper messageWrapper = new DelayMessageWrapper(System.currentTimeMillis(), delaySeconds, delaySeconds - delayLevelEnum.getSeconds(), data);
            if (messageWrapper.getExpireTimeout() > 0 && !tag.startsWith(DELAY_CYCLE_PREFIX)) {
                tag = DELAY_CYCLE_PREFIX + tag;
            }
            if (messageWrapper.getExpireTimeout() <= 0 && tag.startsWith(DELAY_CYCLE_PREFIX)) {
                tag = tag.substring(tag.indexOf(DELAY_CYCLE_PREFIX) + DELAY_CYCLE_PREFIX.length());
            }
            sendDelayMessage(tag, key, JSONObject.toJSONString(messageWrapper).getBytes(StandardCharsets.UTF_8), delayLevelEnum.getLevel());
        } else {
            tag = tag.substring(tag.indexOf(DELAY_CYCLE_PREFIX) + DELAY_CYCLE_PREFIX.length());
            sendAsyncMessage(DELAY_MESSAGE_TOPIC, tag, key, data.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * 异步发送延迟消息
     *
     * @param tag 标签
     * @param key 键
     */
    public void sendDelayMessage(String tag, String key, DelayMessageWrapper messageWrapper) {
        log.info("发送延时消息,tag:{} key:{} message:{}", tag, key, JSONObject.toJSONString(messageWrapper));
        if (messageWrapper.getExpireTimeout() > 0) {
            DelayLevelEnum delayLevelEnum = DelayLevelEnum.findNearbyLevel(messageWrapper.getExpireTimeout());
            if (!tag.startsWith(DELAY_CYCLE_PREFIX)) {
                tag = DELAY_CYCLE_PREFIX + tag;
            }
            messageWrapper.setExpireTimeout(messageWrapper.getExpireTimeout() - delayLevelEnum.getSeconds());
            sendDelayMessage(tag, key, JSONObject.toJSONString(messageWrapper).getBytes(StandardCharsets.UTF_8), delayLevelEnum.getLevel());
        } else {
            tag = tag.substring(tag.indexOf(DELAY_CYCLE_PREFIX) + DELAY_CYCLE_PREFIX.length());
            sendAsyncMessage(DELAY_MESSAGE_TOPIC, tag, key, JSONObject.toJSONString(messageWrapper).getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * 异步发送延迟消息
     *
     * @param tag  标签
     * @param keys 键值
     * @param body 信息
     */
    public void sendDelayMessage(String tag, String keys, byte[] body, int delayLevel) {
        Message msg = new Message(DELAY_MESSAGE_TOPIC, tag, keys, body);
        msg.setDelayTimeLevel(delayLevel);
        try {
            log.info("异步发送延迟消息：{}", msg);
            transactionMQProducer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("异步延迟消息推送成功! {} ", sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    int resendNum = retry;
                    while (resendNum-- > 0) {
                        try {
                            log.info("消息异步推送失败{}! 重新推{} 尝试切换为同步消息重试推送", msg, new String(body, StandardCharsets.UTF_8));
                            SendResult sendResult = transactionMQProducer.send(msg);
                            if (sendResult != null && "SEND_OK".equals(sendResult.getSendStatus().name())) {
                                log.info("重试推送成功! {} 第 {} 次", sendResult, retry - resendNum);
                                return;
                            } else {
                                log.error("推送失敗，抛出异常,同步重试[{}]!", sendResult);
                                throw new Exception();
                            }
                        } catch (Exception e) {
                            log.error("sendDelayMessage occur Exception！", e);
                            if (resendNum > 0) {
                                log.info("尝试重新发送消息 {} 数据格式化 {}", msg, new String(body, StandardCharsets.UTF_8));
                            }
                        }
                    }
                    log.warn("经过多次尝试,最终重试推送失败! msg {}", msg);
                }
            });
        } catch (Exception e) {
            log.error("异步推送消息异常 ", e);
        }
    }

    public void sendAsyncMessage(String topic, String tag, String keys, byte[] body) {
        Message msg = new Message(topic, tag, keys, body);
        transactionMQProducer.setRetryTimesWhenSendAsyncFailed(3);
        try {
            transactionMQProducer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("异步消息推送成功! {} ", sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    int resendNum = retry;
                    while (resendNum-- > 0) {
                        try {
                            log.info("消息异步推送失败{}! 重新推{}", msg, new String(body, StandardCharsets.UTF_8));
                            Thread.sleep(1000L * (retry - resendNum));
                            log.info("尝试切换为同步消息重试推送! {}", msg);
                            transactionMQProducer.setRetryTimesWhenSendFailed(3);
                            SendResult sendResult = transactionMQProducer.send(msg, 1000);
                            if (sendResult != null && "SEND_OK".equals(sendResult.getSendStatus().name())) {
                                log.info("重试推送成功! {} 第 {} 次", sendResult, retry - resendNum);
                                return;
                            } else {
                                log.info("推送失敗，抛出异常,同步重试[{}]!", sendResult);
                                throw new Exception();
                            }
                        } catch (Exception e) {
                            log.error("Occur Exception！", e);
                            if (resendNum > 0) {
                                log.info("尝试重新发送消息 {} 数据格式化 {}", msg, new String(body, StandardCharsets.UTF_8));
                            }
                        }
                    }
                    log.info("经过多次尝试,最终重试推送失败! msg {}", msg);
                }
            });
        } catch (Exception e) {
            log.error("异步推送消息异常 ", e);
        }
    }

}

