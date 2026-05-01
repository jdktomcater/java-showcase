package com.jdktomcat.showcase.spring.ai.mcp.gateway.security;

import com.jdktomcat.showcase.spring.ai.mcp.gateway.config.McpGatewayProperties;

import java.util.List;

public record GatewayPrincipal(
        String name,
        String apiKey,
        List<String> allowedTools,
        McpGatewayProperties.RateLimit rateLimit
) {
}
