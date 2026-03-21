package com.jdktomcat.showcase.ai.langgraph4j.config;

import com.jdktomcat.showcase.ai.langgraph4j.domain.dto.AnalysisTaskState;
import com.jdktomcat.showcase.ai.langgraph4j.domain.dto.CommitTaskState;
import com.jdktomcat.showcase.ai.langgraph4j.service.agent.analysis.*;
import com.jdktomcat.showcase.ai.langgraph4j.service.agent.commit.*;
import com.jdktomcat.showcase.ai.langgraph4j.service.edge.AnalysisRetryEdgeAction;
import com.jdktomcat.showcase.ai.langgraph4j.service.edge.CommitRetryEdgeAction;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.action.AsyncEdgeAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static org.bsc.langgraph4j.GraphDefinition.END;
import static org.bsc.langgraph4j.GraphDefinition.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * 多智能体流程配置：用 LangGraph4j 定义节点流转规则
 */
@Configuration
public class AgentWorkflowConfig {

    @Bean
    public CompiledGraph<AnalysisTaskState> analysisWorkflow(
            TaskDecompositionAgent decompositionAgent,
            DataQueryAgent dataQueryAgent,
            DataAnalysisAgent dataAnalysisAgent,
            ReportGenerationAgent reportGenerationAgent,
            ResultValidationAgent validationAgent,
            AnalysisRetryEdgeAction retryEdgeAction,
            AnalysisTaskStateFactory stateFactory
    ) throws GraphStateException {

        // 1. 创建状态图构建器（指定状态类型）
        StateGraph<AnalysisTaskState> graphBuilder = new StateGraph<>(stateFactory);

        // 2. 添加节点（每个智能体对应一个节点）
        // 任务拆解
        graphBuilder.addNode("decompose", node_async(decompositionAgent));
        // 数据查询
        graphBuilder.addNode("query", node_async(dataQueryAgent));
        // 数据分析
        graphBuilder.addNode("analyze", node_async(dataAnalysisAgent));
        // 报告生成
        graphBuilder.addNode("generateReport",node_async(reportGenerationAgent));
        // 结果校验
        graphBuilder.addNode("validate", node_async(validationAgent));

        // 3. 定义流程流转规则（边）
        // 起始节点 → 任务拆解节点
        graphBuilder.addEdge(START, "decompose");

        // 任务拆解 → 数据查询 → 数据分析 → 报告生成 → 结果校验
        graphBuilder.addEdge("decompose", "query");
        graphBuilder.addEdge("query", "analyze");
        graphBuilder.addEdge("analyze", "generateReport");
        graphBuilder.addEdge("generateReport", "validate");

        // 结果校验 → 分支判断（符合则结束，不符合则重试任务拆解）
        graphBuilder.addConditionalEdges("validate",
                // 分支判断逻辑：根据 needRetry 决定下一个节点
                AsyncEdgeAction.edge_async(retryEdgeAction),
                // 映射：判断结果 → 目标节点（重试→任务拆解，结束→终止流程）
                Map.of(
                        "retry", "decompose",
                        "end", END
                )
        );
        // 4. 构建并返回状态图
        return graphBuilder.compile();
    }

    @Bean
    public CompiledGraph<CommitTaskState> commitWorkflow(
            BusinessAgent businessAgent,
            ConventionAgent conventionAgent,
            PerformanceAgent performanceAgent,
            SecurityAgent securityAgent,
            CommitResultAgent commitResultAgent,
            CommitRetryEdgeAction commitRetryEdgeAction,
            CommitTaskStateFactory stateFactory
    ) throws GraphStateException {

        // 1. 创建状态图构建器（指定状态类型）
        StateGraph<CommitTaskState> graphBuilder = new StateGraph<>(stateFactory);

        // 2. 添加节点（每个智能体对应一个节点）
        // 业务
        graphBuilder.addNode("business", node_async(businessAgent));
        // 编码
        graphBuilder.addNode("convention", node_async(conventionAgent));
        // 性能
        graphBuilder.addNode("performance", node_async(performanceAgent));
        // 安全
        graphBuilder.addNode("security",node_async(securityAgent));
        // 汇总
        graphBuilder.addNode("result", node_async(commitResultAgent));
        // 重试分发。LangGraph4j 允许从 START 出发，但不允许条件边映射回 START。
        graphBuilder.addNode("dispatchReview", node_async(CommitTaskState::toMap));

        // 3. 定义流程流转规则（边）
        // 起始节点 → 分发节点 → 审核节点（扇出）
        graphBuilder.addEdge(START, "dispatchReview");
        graphBuilder.addEdge("dispatchReview", "business");
        graphBuilder.addEdge("dispatchReview", "convention");
        graphBuilder.addEdge("dispatchReview", "performance");
        graphBuilder.addEdge("dispatchReview", "security");
        // 审核节点 → 汇总（扇入）
        graphBuilder.addEdge("business", "result");
        graphBuilder.addEdge("convention", "result");
        graphBuilder.addEdge("performance", "result");
        graphBuilder.addEdge("security", "result");

        // 结果校验 → 分支判断（符合则结束，不符合则重试任务拆解）
        graphBuilder.addConditionalEdges("result",
                // 分支判断逻辑：根据 needRetry 决定下一个节点
                AsyncEdgeAction.edge_async(commitRetryEdgeAction),
                // 映射：判断结果 → 目标节点（重试→任务拆解，结束→终止流程）
                Map.of(
                        "retry", "dispatchReview",
                        "end", END
                )
        );
        // 4. 构建并返回状态图
        return graphBuilder.compile();
    }
}
