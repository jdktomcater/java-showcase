package com.jdktomcat.showcase.spring.ai.mcp.gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ConfigurationProperties(prefix = "mcp.gateway")
public class McpGatewayProperties {

    private String name = "mcp-gateway";
    private String version = "1.0.0";
    private String messageEndpoint = "/mcp/messages";
    private Auth auth = new Auth();
    private List<Upstream> upstreams = new ArrayList<>();

    @Setter
    @Getter
    public static class Auth {
        private boolean enabled = true;
        private String apiKeyHeader = "X-API-Key";
        private String apiKeyQueryParam = "api_key";
        private List<Client> clients = new ArrayList<>();

    }

    @Setter
    @Getter
    public static class Client {
        private String name;
        private String apiKey;
        private List<String> allowedTools = new ArrayList<>();
        private RateLimit rateLimit = new RateLimit();

    }

    @Setter
    @Getter
    public static class RateLimit {
        private int capacity = 60;
        private int refillPerMinute = 60;

    }

    @Setter
    @Getter
    public static class Upstream {
        private String name;
        private boolean enabled = true;
        private String baseUrl;
        private String sseEndpoint = "/sse";
        private Duration requestTimeout = Duration.ofSeconds(30);
        private List<Header> headers = new ArrayList<>();

    }

    @Setter
    @Getter
    public static class Header {
        private String name;
        private String value;

    }
}
