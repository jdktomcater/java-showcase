package com.jdktomcat.showcase.spring.ai.mcp.llm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChatClient 装配
 * <p>
 * 关键点：将由 {@code spring-ai-starter-mcp-client} 自动注册的
 * {@link ToolCallbackProvider}（默认实现是 {@code SyncMcpToolCallbackProvider}）
 * 作为默认工具集挂到 {@link ChatClient} 上。
 * <p>
 * 这样 LLM 在每一次对话时都能「看到」远端 MCP Server 暴露的所有工具，
 * 并由模型自行决策是否、以及如何调用它们（典型的 Function/Tool Calling 流程）。
 */
@Slf4j
@Configuration
public class ChatClientConfig {

    private static final String DEFAULT_SYSTEM_PROMPT = """
            你是一名严谨的数据分析助手，可以使用 MCP 工具查询业务数据库。

            可用工具：
              - listTables()                              查询当前库所有表名
              - describeTable(tableName)                  查询某张表的结构
              - executeSelect(sql, maxRows)               执行只读 SQL（仅 SELECT/SHOW/DESC）

            回答规则：
              1. 在回答用户问题前，先调用工具获取真实数据，禁止凭空编造结果；
              2. 如果不清楚表结构，先调用 listTables / describeTable 探查；
              3. 生成的 SQL 必须是只读语句，并尽量使用 LIMIT 控制返回行数；
              4. 最终回复使用中文，先给出结论，再附上关键数据要点。
            """;

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder,ToolCallbackProvider toolCallbackProvider) {
        log.info("初始化 ChatClient，注入 MCP ToolCallbackProvider: {}", toolCallbackProvider.getClass().getSimpleName());
        return chatClientBuilder.defaultSystem(DEFAULT_SYSTEM_PROMPT).defaultToolCallbacks(toolCallbackProvider).build();
    }
}
