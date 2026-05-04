package com.jdktomcat.showcase.spring.ai.mcp.skywalking;

import com.jdktomcat.showcase.spring.ai.mcp.skywalking.config.SkyWalkingProperties;
import com.jdktomcat.showcase.spring.ai.mcp.skywalking.tool.SkyWalkingQueryTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * MCP SkyWalking Server 启动类。
 */
@SpringBootApplication
@EnableConfigurationProperties(SkyWalkingProperties.class)
public class McpSkyWalkingServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpSkyWalkingServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider skyWalkingToolCallbackProvider(SkyWalkingQueryTools skyWalkingQueryTools) {
        return MethodToolCallbackProvider.builder().toolObjects(skyWalkingQueryTools).build();
    }
}
