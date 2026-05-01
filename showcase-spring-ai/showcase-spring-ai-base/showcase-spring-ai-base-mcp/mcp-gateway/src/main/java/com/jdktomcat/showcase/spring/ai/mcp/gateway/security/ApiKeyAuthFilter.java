package com.jdktomcat.showcase.spring.ai.mcp.gateway.security;

import com.jdktomcat.showcase.spring.ai.mcp.gateway.config.McpGatewayProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final McpGatewayProperties properties;
    private final TokenBucketRateLimiter rateLimiter;
    private final Map<String, McpGatewayProperties.Client> clientsByApiKey;

    public ApiKeyAuthFilter(McpGatewayProperties properties, TokenBucketRateLimiter rateLimiter) {
        this.properties = properties;
        this.rateLimiter = rateLimiter;
        this.clientsByApiKey = properties.getAuth().getClients().stream()
                .filter(client -> client.getApiKey() != null && !client.getApiKey().isBlank())
                .collect(Collectors.toUnmodifiableMap(McpGatewayProperties.Client::getApiKey, Function.identity()));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !properties.getAuth().isEnabled()
                || path.equals("/actuator/health")
                || path.startsWith("/actuator/health/")
                || path.equals("/actuator/info");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String apiKey = resolveApiKey(request);
        if (apiKey == null || apiKey.isBlank()) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "missing or invalid api key");
            return;
        }
        McpGatewayProperties.Client client = clientsByApiKey.get(apiKey);
        if (client == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "missing or invalid api key");
            return;
        }

        GatewayPrincipal principal = new GatewayPrincipal(
                Objects.requireNonNullElse(client.getName(), "anonymous"),
                apiKey,
                client.getAllowedTools(),
                client.getRateLimit()
        );
        if (!rateLimiter.tryAcquire(principal)) {
            writeError(response, 429, "rate limit exceeded");
            return;
        }

        request.setAttribute(GatewaySecurityContext.PRINCIPAL_ATTRIBUTE, principal);
        filterChain.doFilter(request, response);
    }

    private String resolveApiKey(HttpServletRequest request) {
        String apiKey = request.getHeader(properties.getAuth().getApiKeyHeader());
        if (apiKey != null && !apiKey.isBlank()) {
            return apiKey;
        }
        return request.getParameter(properties.getAuth().getApiKeyQueryParam());
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
