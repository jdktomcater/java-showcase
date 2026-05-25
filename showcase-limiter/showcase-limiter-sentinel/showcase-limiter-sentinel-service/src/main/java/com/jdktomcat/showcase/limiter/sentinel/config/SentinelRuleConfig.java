package com.jdktomcat.showcase.limiter.sentinel.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 编程式初始化 Sentinel 规则。
 *
 * <p>生产环境推荐通过 Sentinel Dashboard 或 Nacos 动态推送规则，
 * 这里用 {@link PostConstruct} 做本地兜底规则，方便离线演示。
 *
 * <h2>规则优先级（Dashboard 推送 > 编程式）</h2>
 * Dashboard 下发的规则会覆盖本类中的规则，重启后恢复编程式规则。
 * 持久化方案：Sentinel + Nacos DataSource，规则写入 Nacos，重启后自动拉取。
 */
@Configuration
public class SentinelRuleConfig {

    // ── Resource 名称常量 ──────────────────────────────────────────────────
    public static final String RESOURCE_QUERY  = "order:query";
    public static final String RESOURCE_PAY    = "order:pay";
    public static final String RESOURCE_HOT    = "order:hot";

    @PostConstruct
    public void init() {
        initFlowRules();
        initDegradeRules();
        initParamFlowRules();
        initSystemRules();
    }

    // ── 1. 流量控制规则 ────────────────────────────────────────────────────
    private void initFlowRules() {
        // order:query  QPS ≤ 2
        FlowRule queryRule = new FlowRule(RESOURCE_QUERY)
                .setCount(2)                             // 阈值 2 QPS
                .setGrade(RuleConstant.FLOW_GRADE_QPS);  // 基于 QPS

        // order:pay  并发线程数 ≤ 1（模拟慢接口保护）
        FlowRule payRule = new FlowRule(RESOURCE_PAY)
                .setCount(1)
                .setGrade(RuleConstant.FLOW_GRADE_THREAD);  // 基于并发线程数

        FlowRuleManager.loadRules(List.of(queryRule, payRule));
    }

    // ── 2. 熔断降级规则 ────────────────────────────────────────────────────
    private void initDegradeRules() {
        // order:pay  慢调用熔断：响应时间 > 500ms 的调用占比超 50% 时熔断 10s
        DegradeRule slowCallRule = new DegradeRule(RESOURCE_PAY)
                .setGrade(RuleConstant.DEGRADE_GRADE_RT)   // 慢调用比例策略
                .setCount(500)                             // 最大响应时间 (ms)
                .setSlowRatioThreshold(0.5)                // 慢调用比例阈值 50%
                .setMinRequestAmount(5)                    // 最小请求数，避免冷启动误判
                .setStatIntervalMs(10_000)                 // 统计窗口 10s
                .setTimeWindow(10);                        // 熔断时长 10s

        // order:pay  异常比例熔断：异常比例超 50% 时熔断 5s
        DegradeRule exceptionRule = new DegradeRule(RESOURCE_PAY)
                .setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO)
                .setCount(0.5)                             // 异常比例 50%
                .setMinRequestAmount(5)
                .setStatIntervalMs(10_000)
                .setTimeWindow(5);

        DegradeRuleManager.loadRules(List.of(slowCallRule, exceptionRule));
    }

    // ── 3. 热点参数限流规则 ────────────────────────────────────────────────
    private void initParamFlowRules() {
        // order:hot  对第 0 个参数 (itemId) 限流：默认 QPS ≤ 1
        // 特例：itemId=100 (爆款) QPS ≤ 5
        ParamFlowItem hotItem = new ParamFlowItem()
                .setObject("100")
                .setClassType(String.class.getName())
                .setCount(5);

        ParamFlowRule hotRule = new ParamFlowRule(RESOURCE_HOT)
                .setParamIdx(0)      // 第 0 个方法参数
                .setCount(1)         // 通用阈值 QPS ≤ 1
                .setParamFlowItemList(List.of(hotItem));  // 特例覆盖

        ParamFlowRuleManager.loadRules(List.of(hotRule));
    }

    // ── 4. 系统自适应限流规则 ──────────────────────────────────────────────
    private void initSystemRules() {
        // 系统入口 QPS 总量 ≤ 100（防止机器过载）
        SystemRule systemRule = new SystemRule();
        systemRule.setQps(100);

        SystemRuleManager.loadRules(List.of(systemRule));
    }
}
