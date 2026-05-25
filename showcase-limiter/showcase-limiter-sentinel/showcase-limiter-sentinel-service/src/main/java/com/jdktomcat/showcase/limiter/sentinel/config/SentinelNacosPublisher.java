package com.jdktomcat.showcase.limiter.sentinel.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

/**
 * 应用启动后将默认规则发布到 Nacos，实现：
 * <ol>
 *   <li>服务重启后自动从 Nacos 拉取规则，规则不丢失。</li>
 *   <li>Dashboard 配置 Nacos 推送模式后，可在 Dashboard 看到并修改规则。</li>
 * </ol>
 *
 * <p><b>Dashboard 集成流程：</b>
 * <pre>
 *   Dashboard（修改规则）→ 推送到 Nacos（data-id）→ 服务监听 Nacos 变更 → 内存规则更新
 *                ↑
 *          本类初始写入默认规则
 * </pre>
 *
 * <p>Nacos 需要在配置 Dashboard 时开启推送模式（修改 Dashboard 源码或使用
 * sentinel-dashboard-nacos 版本），否则 Dashboard 仍走默认 HTTP 推送给服务。
 *
 * <p>如果未启动 Nacos，本类会捕获异常并跳过发布，不影响编程式规则运行。
 */
@Component
public class SentinelNacosPublisher implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SentinelNacosPublisher.class);

    private static final String GROUP = "SENTINEL_GROUP";

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.cloud.sentinel.datasource.flow.nacos.server-addr:localhost:8848}")
    private String nacosServerAddr;

    @Override
    public void run(ApplicationArguments args) {
        try {
            Properties props = new Properties();
            props.put("serverAddr", nacosServerAddr);
            ConfigService configService = NacosFactory.createConfigService(props);

            publishFlowRules(configService);
            publishDegradeRules(configService);
            publishParamFlowRules(configService);

            log.info("Sentinel default rules published to Nacos group={} app={}", GROUP, appName);
        } catch (NacosException e) {
            log.warn("Nacos not available, skipping rule publish. "
                    + "Programmatic rules are still active in-memory. cause={}", e.getMessage());
        }
    }

    private void publishFlowRules(ConfigService cs) throws NacosException {
        FlowRule queryRule = new FlowRule(SentinelRuleConfig.RESOURCE_QUERY)
                .setCount(2)
                .setGrade(RuleConstant.FLOW_GRADE_QPS);

        FlowRule payRule = new FlowRule(SentinelRuleConfig.RESOURCE_PAY)
                .setCount(1)
                .setGrade(RuleConstant.FLOW_GRADE_THREAD);

        String json = JSON.toJSONString(List.of(queryRule, payRule));
        cs.publishConfig(appName + "-flow-rules", GROUP, json, "json");
    }

    private void publishDegradeRules(ConfigService cs) throws NacosException {
        DegradeRule slowCallRule = new DegradeRule(SentinelRuleConfig.RESOURCE_PAY)
                .setGrade(RuleConstant.DEGRADE_GRADE_RT)
                .setCount(500)
                .setSlowRatioThreshold(0.5)
                .setMinRequestAmount(5)
                .setStatIntervalMs(10_000)
                .setTimeWindow(10);

        String json = JSON.toJSONString(List.of(slowCallRule));
        cs.publishConfig(appName + "-degrade-rules", GROUP, json, "json");
    }

    private void publishParamFlowRules(ConfigService cs) throws NacosException {
        ParamFlowItem hotItem = new ParamFlowItem()
                .setObject("100")
                .setClassType(String.class.getName())
                .setCount(5);

        ParamFlowRule hotRule = new ParamFlowRule(SentinelRuleConfig.RESOURCE_HOT)
                .setParamIdx(0)
                .setCount(1)
                .setParamFlowItemList(List.of(hotItem));

        String json = JSON.toJSONString(List.of(hotRule));
        cs.publishConfig(appName + "-param-flow-rules", GROUP, json, "json");
    }
}
