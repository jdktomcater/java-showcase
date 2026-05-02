package com.jdktomcat.showcase.spring.ai.mcp.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MCP + LLM 示例启动类
 * <p>
 * 演示「大模型 + MCP」的端到端调用闭环：
 * <ol>
 *   <li>{@code spring-ai-starter-model-ollama} 提供本地 LLM 推理；</li>
 *   <li>{@code spring-ai-starter-mcp-client} 通过 SSE 连接 {@code mcp-gateway}，
 *       再由网关路由到 {@code mcp-mysql-server}，并把远端 Tool 自动适配为 Spring AI {@code ToolCallback}；</li>
 *   <li>{@link com.jdktomcat.showcase.spring.ai.mcp.llm.config.ChatClientConfig}
 *       将这些 ToolCallback 装配进 {@code ChatClient}，由 LLM 自主决定何时调用哪个 Tool；</li>
 *   <li>{@link com.jdktomcat.showcase.spring.ai.mcp.llm.controller.AskController}
 *       暴露 REST 接口接收用户的自然语言提问。</li>
 * </ol>
 */
@Slf4j
@SpringBootApplication
public class McpMysqlLlmApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpMysqlLlmApplication.class, args);
    }
}
