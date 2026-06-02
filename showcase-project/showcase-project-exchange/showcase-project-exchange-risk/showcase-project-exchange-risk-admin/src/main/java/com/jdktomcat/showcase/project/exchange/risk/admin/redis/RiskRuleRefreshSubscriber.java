package com.jdktomcat.showcase.project.exchange.risk.admin.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdktomcat.showcase.project.exchange.risk.admin.config.RiskRuleRedisProperties;
import com.jdktomcat.showcase.project.exchange.risk.common.redis.RiskRuleRefreshMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "exchange.risk.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RiskRuleRefreshSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            RiskRuleRefreshMessage refreshMessage = objectMapper.readValue(
                    message.getBody(), RiskRuleRefreshMessage.class);
            log.info("Received risk rule refresh event: version={}, changeType={}, ruleCode={}, operator={}",
                    refreshMessage.getVersion(),
                    refreshMessage.getChangeType(),
                    refreshMessage.getRuleCode(),
                    refreshMessage.getOperator());
        } catch (Exception ex) {
            log.warn("Failed to parse risk rule refresh message: {}", new String(message.getBody()), ex);
        }
    }
}
