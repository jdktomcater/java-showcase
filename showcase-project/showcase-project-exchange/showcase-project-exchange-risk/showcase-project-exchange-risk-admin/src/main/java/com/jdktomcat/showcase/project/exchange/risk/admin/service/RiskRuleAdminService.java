package com.jdktomcat.showcase.project.exchange.risk.admin.service;

import com.jdktomcat.showcase.project.exchange.risk.admin.domain.RiskRuleChangeLogEntity;
import com.jdktomcat.showcase.project.exchange.risk.admin.domain.RiskRuleEntity;
import com.jdktomcat.showcase.project.exchange.risk.admin.event.RiskRuleChangedEvent;
import com.jdktomcat.showcase.project.exchange.risk.admin.exception.RiskRuleBusinessException;
import com.jdktomcat.showcase.project.exchange.risk.admin.repository.RiskRuleChangeLogRepository;
import com.jdktomcat.showcase.project.exchange.risk.admin.repository.RiskRuleRepository;
import com.jdktomcat.showcase.project.exchange.risk.common.dto.*;
import com.jdktomcat.showcase.project.exchange.risk.common.enums.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskRuleAdminService {

    private final RiskRuleRepository riskRuleRepository;
    private final RiskRuleChangeLogRepository changeLogRepository;
    private final RiskRuleConverter converter;
    private final RiskRuleAuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    public PageResult<RiskRuleDTO> listRules(String ruleType, String scope, String status,
                                             String keyword, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Specification<RiskRuleEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(ruleType)) {
                predicates.add(cb.equal(root.get("ruleType"), ruleType));
            }
            if (StringUtils.hasText(scope)) {
                predicates.add(cb.equal(root.get("scope"), scope));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("ruleCode")), pattern),
                        cb.like(cb.lower(root.get("ruleName")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<RiskRuleEntity> result = riskRuleRepository.findAll(
                spec,
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, "priority", "id"))
        );

        List<RiskRuleDTO> records = result.getContent().stream().map(converter::toDto).toList();
        return PageResult.<RiskRuleDTO>builder()
                .records(records)
                .total(result.getTotalElements())
                .page(safePage)
                .size(safeSize)
                .build();
    }

    public RiskRuleDTO getRule(Long id) {
        RiskRuleEntity entity = findRuleOrThrow(id);
        return converter.toDto(entity);
    }

    @Transactional
    public RiskRuleDTO createRule(RiskRuleCreateRequest request) {
        validateEnums(request.getRuleType(), request.getScope(), request.getAction());
        validateScopeTargets(request.getScope(), request.getTargetUserId(), request.getTargetSymbol());

        if (riskRuleRepository.existsByRuleCode(request.getRuleCode())) {
            throw new RiskRuleBusinessException("规则编码已存在: " + request.getRuleCode());
        }

        RiskRuleEntity entity = new RiskRuleEntity();
        entity.setRuleCode(request.getRuleCode());
        applyMutableFields(entity, request.getRuleName(), request.getRuleType(), request.getScope(),
                request.getTargetUserId(), request.getTargetSymbol(), request.getThresholdValue(),
                request.getThresholdConfig(), request.getAction(), request.getPriority(),
                request.getDescription(), request.getEffectiveFrom(), request.getEffectiveTo());
        entity.setStatus(RuleStatus.ENABLED.getCode());
        entity.setCreatedBy(request.getOperator());
        entity.setUpdatedBy(request.getOperator());

        RiskRuleEntity saved = riskRuleRepository.save(entity);
        auditService.recordCreate(saved, request.getOperator());
        publishChanged("CREATE", saved.getRuleCode(), request.getOperator());
        return converter.toDto(saved);
    }

    @Transactional
    public RiskRuleDTO updateRule(Long id, RiskRuleUpdateRequest request) {
        validateEnums(request.getRuleType(), request.getScope(), request.getAction());
        validateScopeTargets(request.getScope(), request.getTargetUserId(), request.getTargetSymbol());

        RiskRuleEntity entity = findRuleOrThrow(id);
        RiskRuleEntity before = copySnapshot(entity);

        applyMutableFields(entity, request.getRuleName(), request.getRuleType(), request.getScope(),
                request.getTargetUserId(), request.getTargetSymbol(), request.getThresholdValue(),
                request.getThresholdConfig(), request.getAction(), request.getPriority(),
                request.getDescription(), request.getEffectiveFrom(), request.getEffectiveTo());
        entity.setUpdatedBy(request.getOperator());

        RiskRuleEntity saved = riskRuleRepository.save(entity);
        auditService.recordUpdate(before, saved, request.getOperator());
        publishChanged("UPDATE", saved.getRuleCode(), request.getOperator());
        return converter.toDto(saved);
    }

    @Transactional
    public RiskRuleDTO updateStatus(Long id, RiskRuleStatusRequest request) {
        RuleStatus status = RuleStatus.fromCode(request.getStatus());
        RiskRuleEntity entity = findRuleOrThrow(id);
        RiskRuleEntity before = copySnapshot(entity);

        entity.setStatus(status.getCode());
        entity.setUpdatedBy(request.getOperator());

        RiskRuleEntity saved = riskRuleRepository.save(entity);
        auditService.recordStatusChange(before, saved, request.getOperator());
        publishChanged("STATUS", saved.getRuleCode(), request.getOperator());
        return converter.toDto(saved);
    }

    @Transactional
    public void deleteRule(Long id, String operator) {
        RiskRuleEntity entity = findRuleOrThrow(id);
        auditService.recordDelete(entity, operator);
        riskRuleRepository.delete(entity);
        publishChanged("DELETE", entity.getRuleCode(), operator);
    }

    public PageResult<RiskRuleChangeLogDTO> listChangeLogs(Long ruleId, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Page<RiskRuleChangeLogEntity> result = changeLogRepository.findByRuleIdOrderByCreatedAtDesc(
                ruleId, PageRequest.of(safePage, safeSize));
        List<RiskRuleChangeLogDTO> records = result.getContent().stream()
                .map(auditService::toDto)
                .toList();

        return PageResult.<RiskRuleChangeLogDTO>builder()
                .records(records)
                .total(result.getTotalElements())
                .page(safePage)
                .size(safeSize)
                .build();
    }

    public List<RiskRuleTypeInfo> listRuleTypes() {
        return Arrays.stream(RiskRuleType.values())
                .map(type -> new RiskRuleTypeInfo(type.getCode(), type.getLabel(), typeDescription(type)))
                .toList();
    }

    public List<RiskRuleDTO> listEnabledRules() {
        return riskRuleRepository.findByStatusOrderByPriorityAsc(
                        RuleStatus.ENABLED.getCode(),
                        PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "priority", "id")))
                .getContent()
                .stream()
                .map(converter::toDto)
                .toList();
    }

    private void publishChanged(String changeType, String ruleCode, String operator) {
        eventPublisher.publishEvent(new RiskRuleChangedEvent(changeType, ruleCode, operator));
    }

    private RiskRuleEntity findRuleOrThrow(Long id) {
        return riskRuleRepository.findById(id)
                .orElseThrow(() -> new RiskRuleBusinessException("规则不存在: id=" + id));
    }

    private void validateEnums(String ruleType, String scope, String action) {
        RiskRuleType.fromCode(ruleType);
        RiskScope.fromCode(scope);
        RiskAction.fromCode(action);
    }

    private void validateScopeTargets(String scopeCode, String targetUserId, String targetSymbol) {
        RiskScope scope = RiskScope.fromCode(scopeCode);
        switch (scope) {
            case USER -> {
                if (!StringUtils.hasText(targetUserId)) {
                    throw new RiskRuleBusinessException("USER 范围必须指定 targetUserId");
                }
            }
            case SYMBOL -> {
                if (!StringUtils.hasText(targetSymbol)) {
                    throw new RiskRuleBusinessException("SYMBOL 范围必须指定 targetSymbol");
                }
            }
            case USER_SYMBOL -> {
                if (!StringUtils.hasText(targetUserId) || !StringUtils.hasText(targetSymbol)) {
                    throw new RiskRuleBusinessException("USER_SYMBOL 范围必须同时指定 targetUserId 与 targetSymbol");
                }
            }
            case GLOBAL -> {
                // no target required
            }
        }
    }

    private void applyMutableFields(RiskRuleEntity entity, String ruleName, String ruleType, String scope,
                                    String targetUserId, String targetSymbol,
                                    java.math.BigDecimal thresholdValue, String thresholdConfig,
                                    String action, Integer priority, String description,
                                    java.time.LocalDateTime effectiveFrom, java.time.LocalDateTime effectiveTo) {
        entity.setRuleName(ruleName);
        entity.setRuleType(ruleType);
        entity.setScope(scope);
        entity.setTargetUserId(targetUserId);
        entity.setTargetSymbol(targetSymbol);
        entity.setThresholdValue(thresholdValue);
        entity.setThresholdConfig(thresholdConfig);
        entity.setAction(action);
        entity.setPriority(priority);
        entity.setDescription(description);
        entity.setEffectiveFrom(effectiveFrom);
        entity.setEffectiveTo(effectiveTo);
    }

    private RiskRuleEntity copySnapshot(RiskRuleEntity source) {
        RiskRuleEntity copy = new RiskRuleEntity();
        copy.setId(source.getId());
        copy.setRuleCode(source.getRuleCode());
        copy.setRuleName(source.getRuleName());
        copy.setRuleType(source.getRuleType());
        copy.setScope(source.getScope());
        copy.setTargetUserId(source.getTargetUserId());
        copy.setTargetSymbol(source.getTargetSymbol());
        copy.setThresholdValue(source.getThresholdValue());
        copy.setThresholdConfig(source.getThresholdConfig());
        copy.setAction(source.getAction());
        copy.setPriority(source.getPriority());
        copy.setStatus(source.getStatus());
        copy.setDescription(source.getDescription());
        copy.setEffectiveFrom(source.getEffectiveFrom());
        copy.setEffectiveTo(source.getEffectiveTo());
        copy.setCreatedBy(source.getCreatedBy());
        copy.setUpdatedBy(source.getUpdatedBy());
        copy.setCreatedAt(source.getCreatedAt());
        copy.setUpdatedAt(source.getUpdatedAt());
        return copy;
    }

    private String typeDescription(RiskRuleType type) {
        return switch (type) {
            case ORDER_SIZE_LIMIT -> "限制单笔委托数量，thresholdValue 为最大允许数量";
            case DAILY_VOLUME_LIMIT -> "限制用户日累计成交量，thresholdValue 为日上限";
            case ORDER_FREQUENCY -> "限制下单频率，thresholdConfig 示例: {\"windowSeconds\":60,\"maxOrders\":10}";
            case PRICE_DEVIATION -> "限制委托价相对参考价的偏离比例，thresholdValue 为百分比(如 5 表示 5%)";
            case POSITION_LIMIT -> "限制用户在某交易对的最大持仓，thresholdValue 为持仓上限";
            case SYMBOL_RESTRICTION -> "交易对黑白名单，thresholdConfig 示例: {\"mode\":\"BLACKLIST\",\"symbols\":[\"BTCUSDT\"]}";
            case USER_BLACKLIST -> "禁止指定用户交易，targetUserId 必填";
        };
    }
}
