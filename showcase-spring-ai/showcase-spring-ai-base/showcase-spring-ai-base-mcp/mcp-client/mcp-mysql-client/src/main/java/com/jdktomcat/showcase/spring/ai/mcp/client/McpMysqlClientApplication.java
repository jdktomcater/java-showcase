package com.jdktomcat.showcase.spring.ai.mcp.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MCP MySQL Client 启动类
 * <p>
 * 通过 {@code spring-ai-starter-mcp-client} 自动装配 SSE 协议的 MCP Client，
 * 连接到 {@code application.yml} 中配置的 MCP Server，并由
 * {@link com.jdktomcat.showcase.spring.ai.mcp.client.runner.McpClientRunner}
 * 演示工具发现与远程调用流程。
 */
@Slf4j
@SpringBootApplication
public class McpMysqlClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpMysqlClientApplication.class, args);
    }
}
