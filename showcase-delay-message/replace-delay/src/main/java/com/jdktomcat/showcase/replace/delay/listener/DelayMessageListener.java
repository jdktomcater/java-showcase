package com.jdktomcat.showcase.replace.delay.listener;

import com.alibaba.fastjson.JSONObject;
import com.jdktomcat.showcase.replace.delay.component.MessageProducerComponent;
import com.jdktomcat.showcase.replace.delay.component.RedisComponent;
import com.jdktomcat.showcase.replace.delay.config.DelayConfig;
import com.jdktomcat.showcase.replace.delay.wrapper.DelayMessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static com.jdktomcat.showcase.replace.delay.constant.ReplaceDelayConstant.*;

/**
 * 延迟消息消费者
 */
@Slf4j
@Component
public class DelayMessageListener extends BaseConsumerListener {
    /**
     * 消息客户端
     */
    @Autowired
    private MessageProducerComponent messageProducerComponent;

    /**
     * redis相关工具类
     */
    @Autowired
    private RedisComponent redisComponent;

    /**
     * 延迟配置
     */
    @Autowired
    private DelayConfig delayConfig;

    @Override
    protected void consume(String tag, String topic, MessageExt messageExt) {
        if (!delayConfig.isEnableDelayMq()) {
            log.warn("订单状态更新驱动延迟消息开关(order.state.delay.mq.enable)未打开,请确认!");
            return;
        }
        try {
            log.info("延迟消息消费原始內容:{}", toString(messageExt));
            DelayMessageWrapper messageWrapper = JSONObject.parseObject(new String(messageExt.getBody(), StandardCharsets.UTF_8).trim(), DelayMessageWrapper.class);
            if (messageWrapper == null || StringUtils.isEmpty(messageWrapper.getData())) {
                log.warn("延迟消息体格式异常,不做业务处理,请检查！");
                return;
            }
            // 判断标签是否是需要继续延迟等待的标志
            if (tag.startsWith(DELAY_CYCLE_PREFIX)) {
                // 检查是否继续
                log.info("延迟消息未结束，继续延迟！：{}", messageWrapper);
                String entry = messageWrapper.getData();
                if (StringUtils.isEmpty(entry)) {
                    log.warn("延迟消息体存在异常，消息：{}", toString(messageExt));
                    return;
                }
                String[] keyValues = entry.split(KEY_VALUE_SPLIT);
                if (!redisComponent.exist(keyValues[0])) {
                    log.warn("延迟对应的标识：{}已经删除，可以移除相关延迟任务！", keyValues[0]);
                    return;
                }
                messageProducerComponent.sendDelayMessage(tag, messageExt.getKeys(), messageWrapper);
                return;
            }
            // 提取消息体业务数据
            String entry = messageWrapper.getData();
            String[] params = entry.split(KEY_VALUE_SPLIT);
            long pttl = redisComponent.pttl(params[0]);
            log.info("原先redis业务key：{}对应pttl：{}", params[0], pttl);
            if(pttl > delayConfig.getTtlDifferenceTime()){
                log.warn("标签：{} 延迟对应的标识：{}对应的延迟业务发生了更新变化，延迟时间：{}，设置时间间隔：{}，后续业务不做处理！", tag, params[0], pttl, delayConfig.getTtlDifferenceTime());
            }
            long total = messageWrapper.getTotalTimeout() * 1000L;
            long cost = System.currentTimeMillis() - messageWrapper.getCreateTime();
            log.info("延迟消息已经完成延迟，触发预设业务逻辑！预设延迟时间：{}ms 真实耗时：{}ms 误差：{}ms", total, cost, (cost - total));
            long count = redisComponent.incr(MATCH_INCR_KEY + params[0]);
            if (count == 1) {
                redisComponent.expire(MATCH_INCR_KEY + params[0], 10);
                switch (tag) {
                    case DELAY_TAG_UEWF: {
                        boolean del = redisComponent.del(params[0]);
                        log.info("DelayMessageListener consume del:{} result:{}", params[0], del);
                        break;
                    }
                    default:
                        log.warn("该延迟消息tag:{}没有对应的处理逻辑！", tag);
                        break;
                }
            } else {
                log.warn("订单状态驱动处理延迟：{}超时回调存在并发！", params[0]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 订阅tag
     *
     * @return tag组合
     */
    @Override
    public String tags() {
        String[] tags = {DELAY_CYCLE_PREFIX + DELAY_TAG_UEWF, DELAY_TAG_UEWF};
        return String.join(TAG_SPLIT, tags);
    }

    /**
     * 订阅主题
     *
     * @return 主题
     */
    @Override
    public String topic() {
        return DELAY_MESSAGE_TOPIC;
    }
}
