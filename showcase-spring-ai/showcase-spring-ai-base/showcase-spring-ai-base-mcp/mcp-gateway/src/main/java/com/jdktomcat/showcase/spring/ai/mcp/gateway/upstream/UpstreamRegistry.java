package com.jdktomcat.showcase.spring.ai.mcp.gateway.upstream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.config.McpGatewayProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UpstreamRegistry {

    private static final Logger log = LoggerFactory.getLogger(UpstreamRegistry.class);

    private final McpGatewayProperties properties;
    private final ObjectMapper objectMapper;
    private final Map<String, UpstreamMcpClient> clients = new LinkedHashMap<>();
    private final Map<String, ToolDescriptor> toolsByGatewayName = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong();

    public UpstreamRegistry(McpGatewayProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        for (McpGatewayProperties.Upstream upstream : properties.getUpstreams()) {
            if (upstream.isEnabled()) {
                clients.put(upstream.getName(), new UpstreamMcpClient(upstream, objectMapper));
            }
        }
    }

    @PostConstruct
    public void start() {
        clients.values().forEach(UpstreamMcpClient::start);
    }

    @PreDestroy
    public void close() {
        clients.values().forEach(UpstreamMcpClient::close);
    }

    public Collection<ToolDescriptor> tools() {
        if (toolsByGatewayName.isEmpty()) {
            refreshTools();
        }
        return toolsByGatewayName.values().stream()
                .sorted(Comparator.comparing(ToolDescriptor::gatewayToolName))
                .toList();
    }

    public Optional<ToolDescriptor> findTool(String gatewayToolName) {
        ToolDescriptor descriptor = toolsByGatewayName.get(gatewayToolName);
        if (descriptor != null) {
            return Optional.of(descriptor);
        }
        refreshTools();
        return Optional.ofNullable(toolsByGatewayName.get(gatewayToolName));
    }

    public JsonNode call(String upstreamName, JsonNode request) {
        UpstreamMcpClient client = clients.get(upstreamName);
        if (client == null) {
            throw new IllegalArgumentException("unknown upstream: " + upstreamName);
        }
        return client.request(request);
    }

    public List<UpstreamStatus> upstreamStatuses() {
        return clients.values().stream()
                .map(client -> client.status((int) toolsByGatewayName.values().stream()
                        .filter(tool -> tool.upstream().equals(client.name()))
                        .count()))
                .toList();
    }

    public Map<String, Object> refresh() {
        clients.values().forEach(UpstreamMcpClient::restart);
        return refreshTools();
    }

    public synchronized Map<String, Object> refreshTools() {
        Map<String, ToolDescriptor> refreshed = new ConcurrentHashMap<>();
        List<Map<String, Object>> errors = new ArrayList<>();

        for (Map.Entry<String, UpstreamMcpClient> entry : clients.entrySet()) {
            String upstreamName = entry.getKey();
            UpstreamMcpClient client = entry.getValue();
            try {
                JsonNode response = client.request(listToolsRequest());
                JsonNode tools = response.path("result").path("tools");
                if (!tools.isArray()) {
                    continue;
                }
                for (JsonNode upstreamTool : tools) {
                    String upstreamToolName = upstreamTool.path("name").asText();
                    if (upstreamToolName == null || upstreamToolName.isBlank()) {
                        continue;
                    }
                    String gatewayToolName = upstreamName + "." + upstreamToolName;
                    ObjectNode gatewayTool = upstreamTool.deepCopy();
                    gatewayTool.put("name", gatewayToolName);
                    String description = gatewayTool.path("description").asText("");
                    gatewayTool.put("description", "[" + upstreamName + "] " + description);
                    refreshed.put(gatewayToolName, new ToolDescriptor(
                            upstreamName,
                            upstreamToolName,
                            gatewayToolName,
                            gatewayTool
                    ));
                }
            } catch (Exception ex) {
                log.warn("refresh tools from upstream [{}] failed: {}", upstreamName, ex.getMessage());
                errors.add(Map.of("upstream", upstreamName, "error", ex.getMessage()));
            }
        }

        toolsByGatewayName.clear();
        toolsByGatewayName.putAll(refreshed);
        return Map.of(
                "toolCount", refreshed.size(),
                "upstreamCount", clients.size(),
                "errors", errors
        );
    }

    public ArrayNode toolsAsJsonArray() {
        if (toolsByGatewayName.isEmpty()) {
            refreshTools();
        }
        ArrayNode arrayNode = objectMapper.createArrayNode();
        tools().forEach(tool -> arrayNode.add(tool.tool()));
        return arrayNode;
    }

    private ObjectNode listToolsRequest() {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("jsonrpc", "2.0");
        request.put("id", "gw-list-tools-" + idSequence.incrementAndGet());
        request.put("method", "tools/list");
        request.set("params", objectMapper.createObjectNode());
        return request;
    }
}
