package com.jdktomcat.showcase.spring.ai.mcp.skywalking.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdktomcat.showcase.spring.ai.mcp.skywalking.config.SkyWalkingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkyWalkingGraphqlClient {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final SkyWalkingProperties properties;

    public Object executeField(String query, Map<String, Object> variables, String fieldName) {
        Map<String, Object> data = execute(query, variables);
        return data.get(fieldName);
    }

    public Map<String, Object> execute(String query, Map<String, Object> variables) {
        try {
            HttpRequest request = buildRequest(query, variables == null ? Map.of() : variables);
            HttpResponse<String> response = httpClient().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("SkyWalking GraphQL HTTP " + response.statusCode() + ": " + response.body());
            }
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode errors = root.path("errors");
            if (errors.isArray() && !errors.isEmpty()) {
                throw new IllegalStateException("SkyWalking GraphQL errors: " + errors);
            }
            JsonNode data = root.path("data");
            if (!data.isObject()) {
                throw new IllegalStateException("SkyWalking GraphQL response missing data: " + response.body());
            }
            return objectMapper.convertValue(data, MAP_TYPE);
        } catch (IOException ex) {
            throw new IllegalStateException("SkyWalking GraphQL request failed: " + ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("SkyWalking GraphQL request interrupted", ex);
        }
    }

    private HttpRequest buildRequest(String query, Map<String, Object> variables) throws IOException {
        String body = objectMapper.writeValueAsString(Map.of(
                "query", query,
                "variables", variables
        ));
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(properties.getGraphqlEndpoint()))
                .timeout(properties.getRequestTimeout())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));

        List<SkyWalkingProperties.Header> headers = properties.getHeaders();
        for (SkyWalkingProperties.Header header : headers) {
            if (hasText(header.getName()) && hasText(header.getValue())) {
                builder.header(header.getName(), header.getValue());
            }
        }
        return builder.build();
    }

    private HttpClient httpClient() {
        Duration timeout = properties.getRequestTimeout();
        return HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
