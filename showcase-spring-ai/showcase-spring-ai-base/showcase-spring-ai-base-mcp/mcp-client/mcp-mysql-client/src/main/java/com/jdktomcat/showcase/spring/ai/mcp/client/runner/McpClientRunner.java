package com.jdktomcat.showcase.spring.ai.mcp.client.runner;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * MCP Client 演示 Runner
 * <p>
 * 应用启动时执行：
 * <ol>
 *   <li>调用 MCP Server 的 {@code tools/list} 接口，列出所有可用工具；</li>
 *   <li>分别调用 {@code listTables}、{@code describeTable}、{@code executeSelect}
 *       三个 MCP Tool，并把响应内容打印到控制台。</li>
 * </ol>
 * 这里直接使用底层 {@link McpSyncClient}，最贴近 MCP 协议本身（不依赖 LLM），
 * 便于理解 Tool 发现与调用的基础流程。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class McpClientRunner implements CommandLineRunner {

    private final List<McpSyncClient> mcpSyncClients;

    @Override
    public void run(String... args) {
        if (mcpSyncClients == null || mcpSyncClients.isEmpty()) {
            log.warn("未发现任何 MCP Client，请检查 application.yml 中 spring.ai.mcp.client 配置");
            return;
        }

        for (McpSyncClient client : mcpSyncClients) {
            String serverName = client.getServerInfo() != null
                    ? client.getServerInfo().name()
                    : "unknown";
            log.info("==================== MCP Server: {} ====================", serverName);

            ListToolsResult tools = client.listTools();
            log.info("发现 {} 个 MCP Tool：", tools.tools().size());
            for (Tool tool : tools.tools()) {
                log.info("  - {} : {}", tool.name(), tool.description());
            }

            invokeAndPrint(client, "listTables", Map.of());

            invokeAndPrint(client, "describeTable", Map.of("tableName", "t_user"));

            invokeAndPrint(client, "executeSelect", Map.of(
                    "sql", "SELECT u.username, u.city, o.product, o.amount, o.status "
                            + "FROM t_user u JOIN t_order o ON o.user_id = u.id "
                            + "ORDER BY o.amount DESC",
                    "maxRows", 10
            ));
        }
    }

    private void invokeAndPrint(McpSyncClient client, String toolName, Map<String, Object> args) {
        log.info("-------- callTool: {} args={} --------", toolName, args);
        try {
            CallToolResult result = client.callTool(new CallToolRequest(toolName, args));
            if (Boolean.TRUE.equals(result.isError())) {
                log.error("[{}] 调用失败: {}", toolName, result.content());
                return;
            }
            for (McpSchema.Content content : result.content()) {
                if (content instanceof McpSchema.TextContent text) {
                    log.info("[{}] 返回:\n{}", toolName, text.text());
                } else {
                    log.info("[{}] 返回(非文本): {}", toolName, content);
                }
            }
        } catch (Exception ex) {
            log.error("[{}] 调用异常: {}", toolName, ex.getMessage(), ex);
        }
    }
}
