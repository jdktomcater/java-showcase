package com.jdktomcat.showcase.project.exchange.risk.admin.service;

import com.jdktomcat.showcase.project.exchange.risk.admin.domain.RiskRuleEntity;
import com.jdktomcat.showcase.project.exchange.risk.common.dto.RiskRuleDTO;
import com.jdktomcat.showcase.project.exchange.risk.common.enums.RiskAction;
import com.jdktomcat.showcase.project.exchange.risk.common.enums.RiskRuleType;
import com.jdktomcat.showcase.project.exchange.risk.common.enums.RiskScope;
import com.jdktomcat.showcase.project.exchange.risk.common.enums.RuleStatus;
import org.springframework.stereotype.Component;

@Component
public class RiskRuleConverter {

    public RiskRuleDTO toDto(RiskRuleEntity entity) {
        RiskRuleType ruleType = RiskRuleType.fromCode(entity.getRuleType());
        RiskScope scope = RiskScope.fromCode(entity.getScope());
        RiskAction action = RiskAction.fromCode(entity.getAction());
        RuleStatus status = RuleStatus.fromCode(entity.getStatus());

        return RiskRuleDTO.builder()
                .id(entity.getId())
                .ruleCode(entity.getRuleCode())
                .ruleName(entity.getRuleName())
                .ruleType(entity.getRuleType())
                .ruleTypeLabel(ruleType.getLabel())
                .scope(entity.getScope())
                .scopeLabel(scope.getLabel())
                .targetUserId(entity.getTargetUserId())
                .targetSymbol(entity.getTargetSymbol())
                .thresholdValue(entity.getThresholdValue())
                .thresholdConfig(entity.getThresholdConfig())
                .action(entity.getAction())
                .actionLabel(action.getLabel())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .statusLabel(status.getLabel())
                .description(entity.getDescription())
                .effectiveFrom(entity.getEffectiveFrom())
                .effectiveTo(entity.getEffectiveTo())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
