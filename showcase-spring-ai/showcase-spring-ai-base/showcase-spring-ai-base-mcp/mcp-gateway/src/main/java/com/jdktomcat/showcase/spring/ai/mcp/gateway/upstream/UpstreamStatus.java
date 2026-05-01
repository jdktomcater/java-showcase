package com.jdktomcat.showcase.spring.ai.mcp.gateway.upstream;

public record UpstreamStatus(
        String name,
        String baseUrl,
        String sseEndpoint,
        boolean enabled,
        boolean connected,
        boolean messageEndpointReady,
        int cachedToolCount,
        String lastError
) {
}
