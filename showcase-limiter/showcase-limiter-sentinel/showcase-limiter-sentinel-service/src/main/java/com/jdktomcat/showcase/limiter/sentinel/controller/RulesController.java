package com.jdktomcat.showcase.limiter.sentinel.controller;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 调试端点：查看当前服务内存中所有 Sentinel 规则。
 *
 * <p>GET /sentinel/rules
 *
 * <p>用途：
 * <ul>
 *   <li>验证编程式规则是否加载成功（无需 Dashboard）</li>
 *   <li>验证 Nacos DataSource 推送的规则是否已生效</li>
 * </ul>
 *
 * <p>Dashboard 看不到规则的根本原因：Dashboard 管理自己的规则数据库（H2），
 * 通过 HTTP 推送给服务。编程式 {@code FlowRuleManager.loadRules()} 加载的规则
 * 只在服务内存中，Dashboard 不主动拉取，因此显示为空。
 * <br>
 * 解决方案：通过 Dashboard UI 新增规则（会同步入 Dashboard 数据库 + 推送到服务），
 * 或接入 Nacos DataSource（服务从 Nacos 拉取，Dashboard 配置推送模式后往 Nacos 写）。
 */
@RestController
@RequestMapping("/sentinel")
public class RulesController {

    @GetMapping("/rules")
    public Map<String, Object> rules() {
        List<FlowRule>      flowRules      = FlowRuleManager.getRules();
        List<DegradeRule>   degradeRules   = DegradeRuleManager.getRules();
        List<ParamFlowRule> paramFlowRules = ParamFlowRuleManager.getRules();
        List<SystemRule>    systemRules    = SystemRuleManager.getRules();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("flow",      flowRules);
        result.put("degrade",   degradeRules);
        result.put("paramFlow", paramFlowRules);
        result.put("system",    systemRules);
        result.put("total", flowRules.size() + degradeRules.size()
                + paramFlowRules.size() + systemRules.size());
        return result;
    }
}
