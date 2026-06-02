package com.jdktomcat.showcase.project.exchange.risk.admin.web;

import com.jdktomcat.showcase.project.exchange.risk.admin.service.RiskRuleAdminService;
import com.jdktomcat.showcase.project.exchange.risk.common.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin REST API for exchange risk rule management.
 */
@RestController
@RequestMapping("/api/admin/risk")
@RequiredArgsConstructor
public class RiskRuleAdminController {

    private final RiskRuleAdminService riskRuleAdminService;

    @GetMapping("/rules")
    public ApiResult<PageResult<RiskRuleDTO>> listRules(
            @RequestParam(required = false) String ruleType,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(riskRuleAdminService.listRules(ruleType, scope, status, keyword, page, size));
    }

    @GetMapping("/rules/{id}")
    public ApiResult<RiskRuleDTO> getRule(@PathVariable Long id) {
        return ApiResult.ok(riskRuleAdminService.getRule(id));
    }

    @PostMapping("/rules")
    public ApiResult<RiskRuleDTO> createRule(@Valid @RequestBody RiskRuleCreateRequest request) {
        return ApiResult.ok(riskRuleAdminService.createRule(request));
    }

    @PutMapping("/rules/{id}")
    public ApiResult<RiskRuleDTO> updateRule(@PathVariable Long id,
                                             @Valid @RequestBody RiskRuleUpdateRequest request) {
        return ApiResult.ok(riskRuleAdminService.updateRule(id, request));
    }

    @PatchMapping("/rules/{id}/status")
    public ApiResult<RiskRuleDTO> updateStatus(@PathVariable Long id,
                                               @Valid @RequestBody RiskRuleStatusRequest request) {
        return ApiResult.ok(riskRuleAdminService.updateStatus(id, request));
    }

    @DeleteMapping("/rules/{id}")
    public ApiResult<Void> deleteRule(@PathVariable Long id,
                                      @RequestParam(defaultValue = "admin") String operator) {
        riskRuleAdminService.deleteRule(id, operator);
        return ApiResult.ok();
    }

    @GetMapping("/rules/{id}/change-logs")
    public ApiResult<PageResult<RiskRuleChangeLogDTO>> listChangeLogs(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(riskRuleAdminService.listChangeLogs(id, page, size));
    }

    @GetMapping("/rule-types")
    public ApiResult<List<RiskRuleTypeInfo>> listRuleTypes() {
        return ApiResult.ok(riskRuleAdminService.listRuleTypes());
    }

    /**
     * Internal read API for matching / order gateway to pull enabled rules at startup or via refresh.
     */
    @GetMapping("/rules/enabled")
    public ApiResult<List<RiskRuleDTO>> listEnabledRules() {
        return ApiResult.ok(riskRuleAdminService.listEnabledRules());
    }
}
