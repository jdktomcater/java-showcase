package com.jdktomcat.showcase.spring.ai.mcp.gateway.upstream;

import com.fasterxml.jackson.databind.JsonNode;

public record ToolDescriptor(
        String upstream,
        String upstreamToolName,
        String gatewayToolName,
        JsonNode tool
) {
}
