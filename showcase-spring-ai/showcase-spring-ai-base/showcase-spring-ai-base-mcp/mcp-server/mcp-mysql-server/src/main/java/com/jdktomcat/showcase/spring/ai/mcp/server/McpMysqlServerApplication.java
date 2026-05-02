package com.jdktomcat.showcase.spring.ai.mcp.server;

import com.jdktomcat.showcase.spring.ai.mcp.server.tool.MysqlQueryTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * MCP MySQL Server 启动类
 * <p>
 * 通过 {@code spring-ai-starter-mcp-server-webmvc} 启动一个基于 SSE (Server-Sent Events)
 * 传输协议的 MCP 服务端，对外暴露 {@link MysqlQueryTools} 中以 {@code @Tool} 注解
 * 标注的方法，作为可被任意 MCP Client（含 Claude Desktop、Cursor、Cherry Studio
 * 以及本工程的 mcp-mysql-client）远程调用的工具。
 */
@Slf4j
@SpringBootApplication
public class McpMysqlServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpMysqlServerApplication.class, args);
    }

    /**
     * 将业务工具方法注册为 MCP Tool Callback。
     * <p>
     * 使用 {@link MethodToolCallbackProvider} 将带 {@code @Tool} 注解的方法
     * 反射封装成 {@code ToolCallback}，由 MCP Server 自动配置发布。
     */
    @Bean
    public ToolCallbackProvider mysqlToolCallbackProvider(MysqlQueryTools mysqlQueryTools) {
        return MethodToolCallbackProvider.builder().toolObjects(mysqlQueryTools).build();
    }
}
