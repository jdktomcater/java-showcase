package com.jdktomcat.showcase.ai.code.chunk.controller;

import com.jdktomcat.showcase.ai.code.chunk.dto.CodeSearchHit;
import com.jdktomcat.showcase.ai.code.chunk.dto.CodeSearchRequest;
import com.jdktomcat.showcase.ai.code.chunk.dto.DependencyQueryRequest;
import com.jdktomcat.showcase.ai.code.chunk.dto.GraphViewRequest;
import com.jdktomcat.showcase.ai.code.chunk.dto.GraphViewResponse;
import com.jdktomcat.showcase.ai.code.chunk.dto.ImpactQueryRequest;
import com.jdktomcat.showcase.ai.code.chunk.service.CodeIndexService;
import com.jdktomcat.showcase.ai.code.chunk.service.CodeSearchService;
import com.jdktomcat.showcase.ai.code.chunk.service.DependencyIndexService;
import com.jdktomcat.showcase.ai.code.chunk.service.ImpactAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CodeIndexController {

    private final CodeIndexService codeIndexService;
    private final CodeSearchService codeSearchService;
    private final DependencyIndexService dependencyIndexService;
    private final ImpactAnalysisService impactAnalysisService;

    @PostMapping("/api/code/index")
    public Map<String, Object> index() throws IOException {
        int count = codeIndexService.fullIndex();
        return Map.of(
                "success", true,
                "indexedChunks", count
        );
    }

    @PostMapping("/api/code/search")
    public Map<String, Object> search(@Valid @RequestBody CodeSearchRequest request) {
        List<CodeSearchHit> hits = codeSearchService.search(request.query(), request.resolvedTopK());
        return Map.of(
                "success", true,
                "count", hits.size(),
                "hits", hits
        );
    }

    @PostMapping("/api/code/graph/index")
    public Map<String, Object> indexGraph() throws IOException {
        return dependencyIndexService.fullIndex();
    }

    @PostMapping("/api/code/dependencies")
    public Map<String, Object> dependencies(@Valid @RequestBody DependencyQueryRequest request) {
        List<Map<String, Object>> dependencies = impactAnalysisService.dependencies(
                request.resolvedNodeType(),
                request.fqn(),
                request.resolvedDepth()
        );
        return Map.of(
                "success", true,
                "target", request.resolvedNodeType() + ":" + request.fqn(),
                "count", dependencies.size(),
                "dependencies", dependencies
        );
    }

    @PostMapping("/api/code/impact")
    public Map<String, Object> impact(@Valid @RequestBody ImpactQueryRequest request) {
        List<Map<String, Object>> impacts = impactAnalysisService.impact(
                request.methodFqn(),
                request.resolvedDepth()
        );
        return Map.of(
                "success", true,
                "target", request.methodFqn(),
                "count", impacts.size(),
                "impacts", impacts
        );
    }

    @PostMapping("/api/code/graph/view")
    public GraphViewResponse graphView(@Valid @RequestBody GraphViewRequest request) {
        try {
            return switch (request.resolvedMode()) {
                case "impact" -> impactAnalysisService.impactGraph(request.requiredMethodFqn(), request.resolvedDepth());
                case "dependencies" -> impactAnalysisService.dependencyGraph(
                        request.requiredNodeType(),
                        request.requiredFqn(),
                        request.resolvedDepth()
                );
                case "classgraph" -> impactAnalysisService.fullTypeDependencyGraph();
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported mode: " + request.mode());
            };
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}
