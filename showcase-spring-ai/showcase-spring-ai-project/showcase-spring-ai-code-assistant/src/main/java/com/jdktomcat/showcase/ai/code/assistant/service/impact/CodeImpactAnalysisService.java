package com.jdktomcat.showcase.ai.code.assistant.service.impact;

import com.jdktomcat.showcase.ai.code.assistant.domain.entity.CompareResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeImpactAnalysisService {

    private final RestTemplate restTemplate;

    @Value("${code-chunk.base-url:http://localhost:8081}")
    private String codeChunkBaseUrl;

    @Value("${code-chunk.impact.enabled:true}")
    private boolean impactEnabled;

    @Value("${code-chunk.impact.depth:3}")
    private int impactDepth;

    @Value("${code-chunk.impact.max-types:10}")
    private int maxTypes;

    @Value("${code-chunk.impact.max-items-per-type:10}")
    private int maxItemsPerType;

    /**
     * 构建依赖图影响面摘要（使用代码依赖图 API）
     */
    public String buildGraphImpactSummary(String repository, CompareResponse compareResponse) {
        if (!impactEnabled) {
            return "依赖图影响面分析未启用。";
        }
        if (compareResponse == null || compareResponse.getFiles() == null || compareResponse.getFiles().isEmpty()) {
            return "本次提交无可用 diff 文件，无法结合依赖图评估影响面。";
        }

        List<String> changedTypes = extractChangedJavaTypes(compareResponse.getFiles());
        if (changedTypes.isEmpty()) {
            return "本次提交未包含可映射到 Java 类型的工程代码变更。";
        }

        StringBuilder summary = new StringBuilder("## 代码依赖图影响面摘要\n");
        summary.append("- 仓库：").append(safe(repository)).append('\n');
        summary.append("- 变更类型数：").append(changedTypes.size()).append('\n');

        boolean hasData = false;
        for (String typeFqn : changedTypes) {
            try {
                DependencyResponse dependencyResponse = fetchTypeDependencies(typeFqn);
                ImpactResponse impactResponse = fetchTypeImpact(typeFqn);
                List<Map<String, Object>> dependencies = limit(dependencyResponse.getDependencies());
                List<Map<String, Object>> impacts = limit(impactResponse.getImpacts());

                summary.append("\n### ").append(typeFqn).append('\n');
                if (dependencies.isEmpty() && impacts.isEmpty()) {
                    summary.append("- 未在依赖图中发现明显工程内关联节点\n");
                    continue;
                }

                hasData = true;
                appendItems(summary, "下游依赖（被当前类型依赖）", dependencies);
                appendItems(summary, "上游影响（当前类型依赖）", impacts);
            } catch (Exception ex) {
                log.warn("读取代码依赖图失败 type={} url={}", typeFqn, codeChunkBaseUrl, ex);
                summary.append("\n### ").append(typeFqn).append('\n')
                        .append("- 依赖图查询失败：").append(ex.getMessage()).append('\n');
            }
        }

        if (!hasData) {
            summary.append("\n- 暂未识别到明确的工程内扩散路径，请结合 diff 人工判断。");
        }
        return summary.toString();
    }

    /**
     * 构建影响链路摘要（使用 Impact Chain API）
     */
    public String buildImpactChainSummary(String repository, CompareResponse compareResponse) {
        if (!impactEnabled) {
            return "影响链路分析未启用。";
        }
        if (compareResponse == null || compareResponse.getFiles() == null || compareResponse.getFiles().isEmpty()) {
            return "本次提交无可用 diff 文件。";
        }

        List<String> changedTypes = extractChangedJavaTypes(compareResponse.getFiles());
        if (changedTypes.isEmpty()) {
            return "未包含可映射到 Java 类型的变更。";
        }

        StringBuilder summary = new StringBuilder("## 业务影响链路摘要\n");
        summary.append("- 仓库：").append(safe(repository)).append('\n');
        summary.append("- 入口点分析：").append(changedTypes.size()).append(" 个类型\n");

        // 尝试匹配入口点
        List<EntryPointInfo> entryPoints = findRelatedEntryPoints(changedTypes);
        if (entryPoints.isEmpty()) {
            summary.append("- 未找到与变更类型直接相关的业务入口点\n");
            return summary.toString();
        }

        summary.append("- 关联入口点：").append(entryPoints.size()).append(" 个\n\n");

        for (EntryPointInfo entryPoint : entryPoints) {
            try {
                ImpactChainResult chainResult = fetchImpactChain(entryPoint.getId());

                summary.append("### 入口点：").append(entryPoint.getId()).append('\n');
                summary.append("- 类型：").append(entryPoint.getType()).append('\n');
                summary.append("- 位置：").append(entryPoint.getClassName()).append('#').append(entryPoint.getMethodName()).append('\n');

                if (chainResult.getImpactChain().isEmpty()) {
                    summary.append("- 影响链路：未发现下游依赖\n");
                } else {
                    summary.append("- 影响链路：\n");
                    for (Map<String, Object> item : chainResult.getImpactChain()) {
                        summary.append("  - ").append(safe(item.get("name")))
                                .append(" (").append(safe(item.get("type"))).append(")")
                                .append(" hops=").append(safe(item.get("hops")))
                                .append('\n');
                    }
                }
                summary.append('\n');
            } catch (Exception ex) {
                log.warn("读取影响链路失败 entryPoint={} url={}", entryPoint.getId(), codeChunkBaseUrl, ex);
                summary.append("### ").append(entryPoint.getId()).append('\n')
                        .append("- 查询失败：").append(ex.getMessage()).append('\n');
            }
        }

        return summary.toString();
    }

    /**
     * 构建完整的影响面评估报告（结合依赖图和影响链路）
     */
    public String buildFullImpactSummary(String repository, CompareResponse compareResponse) {
        StringBuilder report = new StringBuilder();
        report.append("# 代码变更影响面评估报告\n\n");
        report.append("## 变更概览\n");
        report.append("- 仓库：").append(safe(repository)).append('\n');
        report.append("- 变更文件数：").append(compareResponse != null && compareResponse.getFiles() != null ? compareResponse.getFiles().size() : 0).append("\n\n");
        
        // 1. 代码依赖图影响面
        report.append(buildGraphImpactSummary(repository, compareResponse));
        report.append("\n\n---\n\n");
        
        // 2. 业务影响链路
        report.append(buildImpactChainSummary(repository, compareResponse));
        
        return report.toString();
    }

    private List<EntryPointInfo> findRelatedEntryPoints(List<String> changedTypes) {
        try {
            EntryPointListResponse entryPointResponse = fetchAllEntryPoints();
            List<EntryPointInfo> allEntryPoints = entryPointResponse.getEntryPoints().stream()
                    .map(ep -> {
                        EntryPointInfo info = new EntryPointInfo();
                        info.setId(ep.get("id").toString());
                        info.setType(ep.get("type").toString());
                        info.setClassName(ep.get("className").toString());
                        info.setMethodName(ep.get("methodName").toString());
                        return info;
                    })
                    .toList();

            Set<EntryPointInfo> matched = new LinkedHashSet<>();
            for (String typeFqn : changedTypes) {
                for (EntryPointInfo entryPoint : allEntryPoints) {
                    if (entryPoint.getClassName().equals(typeFqn) ||
                        entryPoint.getClassName().startsWith(typeFqn + "$") ||
                        typeFqn.contains(entryPoint.getClassName())) {
                        matched.add(entryPoint);
                    }
                }
                if (matched.size() >= maxTypes) {
                    break;
                }
            }
            return new ArrayList<>(matched);
        } catch (Exception e) {
            log.warn("获取入口点列表失败", e);
            return List.of();
        }
    }

    private DependencyResponse fetchTypeDependencies(String fqn) {
        return postForObject("/api/code/type-dependencies", new GraphQueryRequest(fqn, impactDepth), DependencyResponse.class);
    }

    private ImpactResponse fetchTypeImpact(String fqn) {
        return postForObject("/api/code/type-impact", new GraphQueryRequest(fqn, impactDepth), ImpactResponse.class);
    }

    private ImpactChainResult fetchImpactChain(String entryPointId) {
        return postForObject("/api/impact/chain", new ImpactChainQuery(entryPointId, impactDepth), ImpactChainResult.class);
    }

    private EntryPointListResponse fetchAllEntryPoints() {
        return getForObject("/api/impact/entry-points", EntryPointListResponse.class);
    }

    private <T> T postForObject(String path, Object request, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        T response = restTemplate.postForObject(codeChunkBaseUrl + path, new HttpEntity<>(request, headers), responseType);
        if (response == null) {
            throw new IllegalStateException("依赖图服务返回空响应：" + path);
        }
        return response;
    }

    private <T> T getForObject(String path, Class<T> responseType) {
        return restTemplate.getForObject(codeChunkBaseUrl + path, responseType);
    }

    private void appendItems(StringBuilder summary, String title, List<Map<String, Object>> items) {
        summary.append("- ").append(title).append("：");
        if (items.isEmpty()) {
            summary.append("无\n");
            return;
        }
        summary.append('\n');
        for (Map<String, Object> item : items) {
            summary.append("  - ")
                    .append(safe(item.get("fqn")))
                    .append(" [").append(safe(item.get("kind"))).append("]")
                    .append(" hops=").append(safe(item.get("hops")))
                    .append(" relations=").append(formatRelationTypes(item.get("relationTypes")))
                    .append('\n');
        }
    }

    private String formatRelationTypes(Object value) {
        if (value instanceof List<?> list && !list.isEmpty()) {
            return list.stream().map(String::valueOf).distinct().reduce((left, right) -> left + " -> " + right).orElse("-");
        }
        return "-";
    }

    private List<String> extractChangedJavaTypes(List<CompareResponse.FileDiff> files) {
        Set<String> types = new LinkedHashSet<>();
        for (CompareResponse.FileDiff file : files) {
            inferJavaTypeFqn(file.getFilename()).ifPresent(types::add);
            if (types.size() >= maxTypes) {
                break;
            }
        }
        return new ArrayList<>(types);
    }

    private Optional<String> inferJavaTypeFqn(String filename) {
        if (filename == null || !filename.endsWith(".java")) {
            return Optional.empty();
        }

        String normalized = filename.replace('\\', '/');
        String marker = normalized.contains("/src/main/java/") ? "/src/main/java/" : "/src/test/java/";
        int index = normalized.indexOf(marker);
        if (index < 0) {
            return Optional.empty();
        }

        String typePath = normalized.substring(index + marker.length(), normalized.length() - ".java".length());
        if (typePath.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(typePath.replace('/', '.'));
    }

    private List<Map<String, Object>> limit(List<Map<String, Object>> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        return items.stream()
                .filter(item -> item.get("filePath") != null)
                .limit(maxItemsPerType)
                .toList();
    }

    private String safe(Object value) {
        return Objects.toString(value, "-");
    }

    // ==================== Records ====================

    private record GraphQueryRequest(
            String fqn,
            Integer depth
    ) {
    }

    private record ImpactChainQuery(
            String entryPointId,
            Integer depth
    ) {
    }

    @Data
    public static class DependencyResponse {
        private boolean success;
        private String target;
        private Integer count;
        private List<Map<String, Object>> dependencies = List.of();
    }

    @Data
    public static class ImpactResponse {
        private boolean success;
        private String target;
        private Integer count;
        private List<Map<String, Object>> impacts = List.of();
    }

    @Data
    public static class ImpactChainResult {
        private boolean success;
        private String entryPointId;
        private String entryPointType;
        private Integer depth;
        private Integer count;
        private List<Map<String, Object>> impactChain = List.of();
        public List<Map<String, Object>> chain() {
            return impactChain;
        }
    }

    @Data
    public static class EntryPointListResponse {
        private boolean success;
        private Integer count;
        private List<Map<String, Object>> entryPoints = List.of();
    }

    @Data
    public static class EntryPointInfo {
        private String id;
        private String type;
        private String className;
        private String methodName;
    }
}
