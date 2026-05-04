package com.jdktomcat.showcase.spring.ai.mcp.skywalking.tool;

import com.jdktomcat.showcase.spring.ai.mcp.skywalking.client.SkyWalkingGraphqlClient;
import com.jdktomcat.showcase.spring.ai.mcp.skywalking.config.SkyWalkingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkyWalkingQueryTools {

    private static final String DEFAULT_STEP = "MINUTE";
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 200;

    private final SkyWalkingGraphqlClient graphqlClient;
    private final SkyWalkingProperties properties;

    @Tool(name = "checkHealth", description = "检查 SkyWalking OAP 健康状态")
    public Object checkHealth() {
        log.info("[MCP-Tool] checkHealth invoked");
        return graphqlClient.executeField("""
                query {
                  checkHealth {
                    score
                    details
                  }
                }
                """, Map.of(), "checkHealth");
    }

    @Tool(name = "getTimeInfo", description = "获取 SkyWalking OAP 当前时间和时区信息")
    public Object getTimeInfo() {
        log.info("[MCP-Tool] getTimeInfo invoked");
        return graphqlClient.executeField("""
                query {
                  getTimeInfo {
                    timezone
                    currentTimestamp
                  }
                }
                """, Map.of(), "getTimeInfo");
    }

    @Tool(name = "listLayers", description = "列出 SkyWalking 当前可用的监控 Layer")
    public Object listLayers() {
        log.info("[MCP-Tool] listLayers invoked");
        return graphqlClient.executeField("query { listLayers }", Map.of(), "listLayers");
    }

    @Tool(name = "listServices", description = "按 Layer 列出 SkyWalking 服务；layer 为空时列出全部服务")
    public Object listServices(@ToolParam(required = false, description = "SkyWalking Layer，例如 GENERAL、K8S、MESH；为空表示全部") String layer) {
        log.info("[MCP-Tool] listServices invoked, layer={}", layer);
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("layer", blankToNull(layer));
        return graphqlClient.executeField("""
                query($layer: String) {
                  listServices(layer: $layer) {
                    id
                    name
                    group
                    shortName
                    layers
                    normal
                  }
                }
                """, variables, "listServices");
    }

    @Tool(name = "findService", description = "按服务名查找 SkyWalking 服务元数据")
    public Object findService(@ToolParam(description = "SkyWalking 服务名") String serviceName) {
        log.info("[MCP-Tool] findService invoked, serviceName={}", serviceName);
        requireText(serviceName, "serviceName");
        return graphqlClient.executeField("""
                query($serviceName: String!) {
                  findService(serviceName: $serviceName) {
                    id
                    name
                    group
                    shortName
                    layers
                    normal
                  }
                }
                """, Map.of("serviceName", serviceName), "findService");
    }

    @Tool(name = "listInstances", description = "列出指定服务在时间范围内的实例")
    public Object listInstances(
            @ToolParam(description = "SkyWalking serviceId，可由 listServices 或 findService 获取") String serviceId,
            @ToolParam(required = false, description = "开始时间，格式随 step 变化；MINUTE 示例：2026-05-04 1015") String start,
            @ToolParam(required = false, description = "结束时间，格式随 step 变化；MINUTE 示例：2026-05-04 1045") String end,
            @ToolParam(required = false, description = "时间粒度：SECOND、MINUTE、HOUR、DAY，默认 MINUTE") String step,
            @ToolParam(required = false, description = "未传 start/end 时向前查询的分钟数，默认 30") Integer lastMinutes) {
        log.info("[MCP-Tool] listInstances invoked, serviceId={}", serviceId);
        requireText(serviceId, "serviceId");
        return graphqlClient.executeField("""
                query($serviceId: ID!, $duration: Duration!) {
                  listInstances(serviceId: $serviceId, duration: $duration) {
                    id
                    name
                    language
                    instanceUUID
                    attributes {
                      name
                      value
                    }
                  }
                }
                """, Map.of("serviceId", serviceId, "duration", duration(start, end, step, lastMinutes)), "listInstances");
    }

    @Tool(name = "findEndpoints", description = "查找指定服务的 Endpoint")
    public Object findEndpoints(
            @ToolParam(description = "SkyWalking serviceId") String serviceId,
            @ToolParam(required = false, description = "Endpoint 关键字，可为空") String keyword,
            @ToolParam(required = false, description = "最大返回数量，默认 20，最大 200") Integer limit,
            @ToolParam(required = false, description = "开始时间，格式随 step 变化") String start,
            @ToolParam(required = false, description = "结束时间，格式随 step 变化") String end,
            @ToolParam(required = false, description = "时间粒度：SECOND、MINUTE、HOUR、DAY，默认 MINUTE") String step,
            @ToolParam(required = false, description = "未传 start/end 时向前查询的分钟数，默认 30") Integer lastMinutes) {
        log.info("[MCP-Tool] findEndpoints invoked, serviceId={}, keyword={}", serviceId, keyword);
        requireText(serviceId, "serviceId");
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("serviceId", serviceId);
        variables.put("keyword", blankToNull(keyword));
        variables.put("limit", normalizeLimit(limit));
        variables.put("duration", duration(start, end, step, lastMinutes));
        return graphqlClient.executeField("""
                query($serviceId: ID!, $keyword: String, $limit: Int!, $duration: Duration) {
                  findEndpoint(serviceId: $serviceId, keyword: $keyword, limit: $limit, duration: $duration) {
                    id
                    name
                  }
                }
                """, variables, "findEndpoint");
    }

    @Tool(name = "getGlobalTopology", description = "查询 SkyWalking 全局服务拓扑")
    public Object getGlobalTopology(
            @ToolParam(required = false, description = "SkyWalking Layer，例如 GENERAL；为空表示全部") String layer,
            @ToolParam(required = false, description = "开始时间，格式随 step 变化") String start,
            @ToolParam(required = false, description = "结束时间，格式随 step 变化") String end,
            @ToolParam(required = false, description = "时间粒度：SECOND、MINUTE、HOUR、DAY，默认 MINUTE") String step,
            @ToolParam(required = false, description = "未传 start/end 时向前查询的分钟数，默认 30") Integer lastMinutes) {
        log.info("[MCP-Tool] getGlobalTopology invoked, layer={}", layer);
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("duration", duration(start, end, step, lastMinutes));
        variables.put("layer", blankToNull(layer));
        return graphqlClient.executeField("""
                query($duration: Duration!, $layer: String) {
                  getGlobalTopology(duration: $duration, layer: $layer) {
                    nodes {
                      id
                      name
                      type
                      isReal
                      layers
                    }
                    calls {
                      id
                      source
                      target
                      sourceComponents
                      targetComponents
                      detectPoints
                    }
                  }
                }
                """, variables, "getGlobalTopology");
    }

    @Tool(name = "queryBasicTraces", description = "按服务、实例、Endpoint、Trace ID 或耗时条件查询 Trace 摘要")
    public Object queryBasicTraces(
            @ToolParam(required = false, description = "SkyWalking serviceId；为空表示所有服务") String serviceId,
            @ToolParam(required = false, description = "SkyWalking serviceInstanceId") String serviceInstanceId,
            @ToolParam(required = false, description = "SkyWalking endpointId") String endpointId,
            @ToolParam(required = false, description = "Trace ID 精确过滤") String traceId,
            @ToolParam(required = false, description = "最小 Trace 耗时，单位毫秒") Integer minTraceDuration,
            @ToolParam(required = false, description = "最大 Trace 耗时，单位毫秒") Integer maxTraceDuration,
            @ToolParam(required = false, description = "Trace 状态：ALL、SUCCESS、ERROR，默认 ALL") String traceState,
            @ToolParam(required = false, description = "排序：BY_START_TIME、BY_DURATION，默认 BY_START_TIME") String queryOrder,
            @ToolParam(required = false, description = "页码，从 1 开始，默认 1") Integer pageNum,
            @ToolParam(required = false, description = "每页数量，默认 20，最大 200") Integer pageSize,
            @ToolParam(required = false, description = "开始时间，格式随 step 变化") String start,
            @ToolParam(required = false, description = "结束时间，格式随 step 变化") String end,
            @ToolParam(required = false, description = "时间粒度：SECOND、MINUTE、HOUR、DAY，默认 MINUTE") String step,
            @ToolParam(required = false, description = "未传 start/end 时向前查询的分钟数，默认 30") Integer lastMinutes) {
        log.info("[MCP-Tool] queryBasicTraces invoked, serviceId={}, traceId={}", serviceId, traceId);
        Map<String, Object> condition = new LinkedHashMap<>();
        putIfText(condition, "serviceId", serviceId);
        putIfText(condition, "serviceInstanceId", serviceInstanceId);
        putIfText(condition, "endpointId", endpointId);
        putIfText(condition, "traceId", traceId);
        putIfPresent(condition, "minTraceDuration", minTraceDuration);
        putIfPresent(condition, "maxTraceDuration", maxTraceDuration);
        condition.put("traceState", enumValue(traceState, "ALL", "ALL", "SUCCESS", "ERROR"));
        condition.put("queryOrder", enumValue(queryOrder, "BY_START_TIME", "BY_START_TIME", "BY_DURATION"));
        condition.put("queryDuration", duration(start, end, step, lastMinutes));
        condition.put("paging", Map.of(
                "pageNum", pageNum == null || pageNum < 1 ? 1 : pageNum,
                "pageSize", normalizeLimit(pageSize)
        ));
        return graphqlClient.executeField("""
                query($condition: TraceQueryCondition) {
                  queryBasicTraces(condition: $condition) {
                    traces {
                      segmentId
                      endpointNames
                      duration
                      start
                      isError
                      traceIds
                    }
                  }
                }
                """, Map.of("condition", condition), "queryBasicTraces");
    }

    @Tool(name = "queryTrace", description = "按 Trace ID 查询完整 Span 明细")
    public Object queryTrace(
            @ToolParam(description = "Trace ID") String traceId,
            @ToolParam(required = false, description = "开始时间，格式随 step 变化；为空默认最近 1 天") String start,
            @ToolParam(required = false, description = "结束时间，格式随 step 变化；为空默认最近 1 天") String end,
            @ToolParam(required = false, description = "时间粒度：SECOND、MINUTE、HOUR、DAY，默认 MINUTE") String step) {
        log.info("[MCP-Tool] queryTrace invoked, traceId={}", traceId);
        requireText(traceId, "traceId");
        return graphqlClient.executeField("""
                query($traceId: ID!, $duration: Duration) {
                  queryTrace(traceId: $traceId, duration: $duration) {
                    spans {
                      traceId
                      segmentId
                      spanId
                      parentSpanId
                      serviceCode
                      serviceInstanceName
                      startTime
                      endTime
                      endpointName
                      type
                      peer
                      component
                      isError
                      layer
                      tags {
                        key
                        value
                      }
                      logs {
                        time
                        data {
                          key
                          value
                        }
                      }
                    }
                  }
                }
                """, Map.of("traceId", traceId, "duration", duration(start, end, step, 24 * 60)), "queryTrace");
    }

    @Tool(name = "listMetrics", description = "列出 SkyWalking 可查询的 Metrics 定义")
    public Object listMetrics(@ToolParam(required = false, description = "按 Metrics 名称正则过滤，例如 service_.*") String regex) {
        log.info("[MCP-Tool] listMetrics invoked, regex={}", regex);
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("regex", blankToNull(regex));
        return graphqlClient.executeField("""
                query($regex: String) {
                  listMetrics(regex: $regex) {
                    name
                    catalog
                    type
                  }
                }
                """, variables, "listMetrics");
    }

    @Tool(name = "execMetricsExpression", description = "执行 SkyWalking MQE Metrics Query Expression")
    public Object execMetricsExpression(
            @ToolParam(description = "SkyWalking MQE 表达式，例如 service_sla 或 avg(service_resp_time)") String expression,
            @ToolParam(required = false, description = "服务名；查询服务级指标时填写") String serviceName,
            @ToolParam(required = false, description = "实例名；查询实例级指标时填写") String serviceInstanceName,
            @ToolParam(required = false, description = "Endpoint 名；查询 Endpoint 级指标时填写") String endpointName,
            @ToolParam(required = false, description = "服务是否为真实服务，默认 true") Boolean normal,
            @ToolParam(required = false, description = "开始时间，格式随 step 变化") String start,
            @ToolParam(required = false, description = "结束时间，格式随 step 变化") String end,
            @ToolParam(required = false, description = "时间粒度：SECOND、MINUTE、HOUR、DAY，默认 MINUTE") String step,
            @ToolParam(required = false, description = "未传 start/end 时向前查询的分钟数，默认 30") Integer lastMinutes) {
        log.info("[MCP-Tool] execMetricsExpression invoked, expression={}", expression);
        requireText(expression, "expression");
        Map<String, Object> entity = new LinkedHashMap<>();
        putIfText(entity, "serviceName", serviceName);
        putIfText(entity, "serviceInstanceName", serviceInstanceName);
        putIfText(entity, "endpointName", endpointName);
        if (normal != null || entity.containsKey("serviceName")) {
            entity.put("normal", normal == null || normal);
        }
        return graphqlClient.executeField("""
                query($expression: String!, $entity: Entity!, $duration: Duration!) {
                  execExpression(expression: $expression, entity: $entity, duration: $duration) {
                    type
                    error
                    results {
                      metric {
                        labels {
                          key
                          value
                        }
                      }
                      values {
                        id
                        value
                        traceID
                        owner {
                          scope
                          serviceID
                          serviceName
                          normal
                          serviceInstanceID
                          serviceInstanceName
                          endpointID
                          endpointName
                        }
                      }
                    }
                  }
                }
                """, Map.of(
                "expression", expression,
                "entity", entity,
                "duration", duration(start, end, step, lastMinutes)
        ), "execExpression");
    }

    private Map<String, Object> duration(String start, String end, String step, Integer lastMinutes) {
        String normalizedStep = enumValue(step, DEFAULT_STEP, "SECOND", "MINUTE", "HOUR", "DAY");
        if (hasText(start) && hasText(end)) {
            return Map.of("start", start, "end", end, "step", normalizedStep);
        }
        ZonedDateTime now = ZonedDateTime.now(properties.getQueryZone());
        ZonedDateTime from = now.minusMinutes(lastMinutes == null || lastMinutes <= 0 ? 30 : lastMinutes);
        DateTimeFormatter formatter = formatter(normalizedStep);
        return Map.of(
                "start", formatter.format(from),
                "end", formatter.format(now),
                "step", normalizedStep
        );
    }

    private DateTimeFormatter formatter(String step) {
        return switch (step) {
            case "SECOND" -> DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss");
            case "HOUR" -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            case "DAY" -> DateTimeFormatter.ofPattern("yyyy-MM-dd");
            default -> DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
        };
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private String enumValue(String value, String defaultValue, String... allowedValues) {
        if (!hasText(value)) {
            return defaultValue;
        }
        String normalized = value.trim().toUpperCase();
        for (String allowedValue : allowedValues) {
            if (allowedValue.equals(normalized)) {
                return normalized;
            }
        }
        throw new IllegalArgumentException("非法枚举值：" + value);
    }

    private void requireText(String value, String name) {
        if (!hasText(value)) {
            throw new IllegalArgumentException(name + " 不能为空");
        }
    }

    private Object blankToNull(String value) {
        return hasText(value) ? value : null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private void putIfText(Map<String, Object> map, String key, String value) {
        if (hasText(value)) {
            map.put(key, value);
        }
    }

    private void putIfPresent(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
