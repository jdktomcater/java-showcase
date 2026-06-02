package com.jdktomcat.showcase.project.exchange.risk.admin.redis;

import com.jdktomcat.showcase.project.exchange.risk.admin.event.RiskRuleChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RiskRuleRedisEventListener {

    private final RiskRuleRedisPublisher redisSyncService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRuleChanged(RiskRuleChangedEvent event) {
        redisSyncService.syncAll(event.changeType(), event.ruleCode(), event.operator());
    }
}
