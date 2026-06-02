package com.jdktomcat.showcase.project.exchange.risk.admin.service;

import com.jdktomcat.showcase.project.exchange.risk.admin.domain.RiskRuleChangeLogEntity;
import com.jdktomcat.showcase.project.exchange.risk.admin.domain.RiskRuleEntity;
import com.jdktomcat.showcase.project.exchange.risk.admin.repository.RiskRuleChangeLogRepository;
import com.jdktomcat.showcase.project.exchange.risk.common.dto.RiskRuleChangeLogDTO;
import com.jdktomcat.showcase.project.exchange.risk.common.dto.RiskRuleCreateRequest;
import com.jdktomcat.showcase.project.exchange.risk.common.dto.RiskRuleUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiskRuleAuditService {

    private final RiskRuleChangeLogRepository changeLogRepository;
    private final RiskRuleConverter converter;

    @Transactional
    public void recordCreate(RiskRuleEntity entity, String operator) {
        save(entity.getId(), entity.getRuleCode(), "CREATE", null, snapshot(entity), operator);
    }

    @Transactional
    public void recordUpdate(RiskRuleEntity before, RiskRuleEntity after, String operator) {
        save(after.getId(), after.getRuleCode(), "UPDATE", snapshot(before), snapshot(after), operator);
    }

    @Transactional
    public void recordStatusChange(RiskRuleEntity before, RiskRuleEntity after, String operator) {
        save(after.getId(), after.getRuleCode(), "STATUS", snapshot(before), snapshot(after), operator);
    }

    @Transactional
    public void recordDelete(RiskRuleEntity entity, String operator) {
        save(entity.getId(), entity.getRuleCode(), "DELETE", snapshot(entity), null, operator);
    }

    public RiskRuleChangeLogDTO toDto(RiskRuleChangeLogEntity entity) {
        return RiskRuleChangeLogDTO.builder()
                .id(entity.getId())
                .ruleId(entity.getRuleId())
                .ruleCode(entity.getRuleCode())
                .changeType(entity.getChangeType())
                .beforeSnapshot(entity.getBeforeSnapshot())
                .afterSnapshot(entity.getAfterSnapshot())
                .operator(entity.getOperator())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private void save(Long ruleId, String ruleCode, String changeType,
                      String before, String after, String operator) {
        RiskRuleChangeLogEntity log = new RiskRuleChangeLogEntity();
        log.setRuleId(ruleId);
        log.setRuleCode(ruleCode);
        log.setChangeType(changeType);
        log.setBeforeSnapshot(before);
        log.setAfterSnapshot(after);
        log.setOperator(operator);
        changeLogRepository.save(log);
    }

    private String snapshot(RiskRuleEntity entity) {
        return converter.toDto(entity).toString();
    }
}
