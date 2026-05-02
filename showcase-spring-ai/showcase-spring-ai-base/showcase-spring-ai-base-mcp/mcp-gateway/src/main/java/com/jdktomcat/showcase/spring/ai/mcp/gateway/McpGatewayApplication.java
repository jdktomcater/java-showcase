package com.jdktomcat.showcase.spring.ai.mcp.gateway;

import com.jdktomcat.showcase.spring.ai.mcp.gateway.config.McpGatewayProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(McpGatewayProperties.class)
public class McpGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpGatewayApplication.class, args);
    }
}
