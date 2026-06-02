package com.jdktomcat.showcase.project.exchange.risk.admin.config;

import com.jdktomcat.showcase.project.exchange.risk.admin.domain.RiskRuleEntity;
import com.jdktomcat.showcase.project.exchange.risk.admin.repository.RiskRuleRepository;
import com.jdktomcat.showcase.project.exchange.risk.common.enums.RiskAction;
import com.jdktomcat.showcase.project.exchange.risk.common.enums.RiskRuleType;
import com.jdktomcat.showcase.project.exchange.risk.common.enums.RiskScope;
import com.jdktomcat.showcase.project.exchange.risk.common.enums.RuleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class RiskRuleDemoDataInitializer implements ApplicationRunner {

    private final RiskRuleRepository riskRuleRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (riskRuleRepository.count() > 0) {
            return;
        }

        RiskRuleEntity orderSizeRule = new RiskRuleEntity();
        orderSizeRule.setRuleCode("GLOBAL_ORDER_SIZE_001");
        orderSizeRule.setRuleName("全平台单笔下单上限");
        orderSizeRule.setRuleType(RiskRuleType.ORDER_SIZE_LIMIT.getCode());
        orderSizeRule.setScope(RiskScope.GLOBAL.getCode());
        orderSizeRule.setThresholdValue(new BigDecimal("1000000"));
        orderSizeRule.setAction(RiskAction.REJECT.getCode());
        orderSizeRule.setPriority(10);
        orderSizeRule.setStatus(RuleStatus.ENABLED.getCode());
        orderSizeRule.setDescription("单笔委托数量不得超过 100 万");
        orderSizeRule.setCreatedBy("system");
        orderSizeRule.setUpdatedBy("system");

        RiskRuleEntity frequencyRule = new RiskRuleEntity();
        frequencyRule.setRuleCode("GLOBAL_ORDER_FREQ_001");
        frequencyRule.setRuleName("全平台下单频率限制");
        frequencyRule.setRuleType(RiskRuleType.ORDER_FREQUENCY.getCode());
        frequencyRule.setScope(RiskScope.GLOBAL.getCode());
        frequencyRule.setThresholdConfig("{\"windowSeconds\":60,\"maxOrders\":20}");
        frequencyRule.setAction(RiskAction.REJECT.getCode());
        frequencyRule.setPriority(20);
        frequencyRule.setStatus(RuleStatus.ENABLED.getCode());
        frequencyRule.setDescription("60 秒内最多 20 笔委托");
        frequencyRule.setCreatedBy("system");
        frequencyRule.setUpdatedBy("system");

        riskRuleRepository.save(orderSizeRule);
        riskRuleRepository.save(frequencyRule);
    }
}
