package com.jdktomcat.showcase.ai.impact.analysis.controller;

import com.jdktomcat.showcase.ai.impact.analysis.domain.EntryPointType;
import com.jdktomcat.showcase.ai.impact.analysis.dto.AnalysisResponse;
import com.jdktomcat.showcase.ai.impact.analysis.dto.EntryPointListResponse;
import com.jdktomcat.showcase.ai.impact.analysis.dto.ImpactChainRequest;
import com.jdktomcat.showcase.ai.impact.analysis.dto.ImpactChainResponse;
import com.jdktomcat.showcase.ai.impact.analysis.dto.ImpactGraphRequest;
import com.jdktomcat.showcase.ai.impact.analysis.dto.ImpactGraphResponse;
import com.jdktomcat.showcase.ai.impact.analysis.repository.ImpactChainRepository;
import com.jdktomcat.showcase.ai.impact.analysis.service.GraphExportService;
import com.jdktomcat.showcase.ai.impact.analysis.service.ImpactChainAnalyzer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * REST controller for impact chain analysis API.
 */
@RestController
@RequestMapping("/api/impact")
@RequiredArgsConstructor
public class ImpactChainController {

    private final ImpactChainAnalyzer analyzer;
    private final GraphExportService graphExportService;

    /**
     * Execute full impact chain analysis.
     * Scans all Java files, detects entry points, builds impact chains, and stores in Neo4j.
     */
    @PostMapping("/analyze")
    public AnalysisResponse analyze() {
        try {
            ImpactChainAnalyzer.AnalysisResult result = analyzer.fullAnalysis();
            return new AnalysisResponse(
                    true,
                    result.entryPointCount(),
                    result.nodeCount(),
                    result.relationCount(),
                    "Analysis completed successfully"
            );
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Analysis failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get all entry points.
     */
    @GetMapping("/entry-points")
    public EntryPointListResponse listEntryPoints() {
        List<Map<String, Object>> entryPoints = analyzer.getAllEntryPoints();
        return new EntryPointListResponse(true, entryPoints.size(), entryPoints);
    }

    /**
     * Get entry points by type.
     */
    @GetMapping("/entry-points/by-type")
    public EntryPointListResponse listEntryPointsByType(@RequestParam EntryPointType type) {
        List<Map<String, Object>> entryPoints = analyzer.getEntryPointsByType(type);
        return new EntryPointListResponse(true, entryPoints.size(), entryPoints);
    }

    /**
     * Query impact chain for a specific entry point.
     */
    @PostMapping("/chain")
    public ImpactChainResponse getImpactChain(@Valid @RequestBody ImpactChainRequest request) {
        if (request.entryPointId() == null || request.entryPointId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "entryPointId is required");
        }

        List<Map<String, Object>> chain = analyzer.queryImpactChain(
                request.entryPointId(),
                request.resolvedDepth()
        );

        // Extract entry point type from the first result if available
        ImpactChainResponse.EntryPointTypeView typeView = ImpactChainResponse.EntryPointTypeView.HTTP;
        if (!chain.isEmpty()) {
            String typeStr = (String) chain.get(0).get("entryPointType");
            if (typeStr != null) {
                try {
                    typeView = ImpactChainResponse.EntryPointTypeView.valueOf(typeStr);
                } catch (IllegalArgumentException e) {
                    // Ignore, use default
                }
            }
        }

        return new ImpactChainResponse(
                true,
                request.entryPointId(),
                typeView,
                request.resolvedDepth(),
                chain.size(),
                chain
        );
    }

    /**
     * Get impact graph for visualization.
     * Supports modes: 'impact' (single entry point), 'full' (all entry points)
     */
    @PostMapping("/graph")
    public ImpactGraphResponse getImpactGraph(@Valid @RequestBody ImpactGraphRequest request) {
        try {
            ImpactChainRepository.GraphData graphData;

            if ("full".equals(request.resolvedMode())) {
                // Full graph filtered by entry point types
                List<EntryPointType> types = request.resolvedEntryPointTypes().stream()
                        .map(EntryPointType::valueOf)
                        .toList();
                graphData = analyzer.getFullImpactGraph(types);
            } else {
                // Single entry point impact graph
                if (request.entryPointId() == null || request.entryPointId().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "entryPointId is required for impact mode");
                }
                graphData = analyzer.getImpactGraph(request.entryPointId(), request.resolvedDepth());
            }

            List<ImpactGraphResponse.NodeView> nodes = graphData.nodes().stream()
                    .map(node -> new ImpactGraphResponse.NodeView(
                            node.id(),
                            node.name(),
                            node.type().name(),
                            node.fqn(),
                            node.kind(),
                            node.filePath(),
                            node.module(),
                            node.startLine(),
                            node.endLine(),
                            node.isEntryPoint()
                    ))
                    .toList();

            List<ImpactGraphResponse.EdgeView> edges = graphData.relations().stream()
                    .map(relation -> new ImpactGraphResponse.EdgeView(
                            relation.fromId() + "->" + relation.toId() + ":" + relation.type().name(),
                            relation.fromId(),
                            relation.toId(),
                            relation.type().name()
                    ))
                    .toList();

            return new ImpactGraphResponse(
                    true,
                    request.resolvedMode(),
                    request.entryPointId() != null ? request.entryPointId() : "TYPE:*",
                    nodes.size(),
                    edges.size(),
                    nodes,
                    edges
            );

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request: " + e.getMessage(), e);
        }
    }

    /**
     * Export impact graph to DOT format (for Graphviz).
     */
    @GetMapping("/export/dot")
    public ResponseEntity<String> exportDot(
            @RequestParam String entryPointId,
            @RequestParam(defaultValue = "5") int depth
    ) {
        try {
            String dotContent = graphExportService.exportToDot(entryPointId, depth);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/vnd.graphviz")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + entryPointId + ".dot\"")
                    .body(dotContent);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Export failed: " + e.getMessage(), e);
        }
    }

    /**
     * Export full impact graph to DOT format.
     */
    @GetMapping("/export/dot/full")
    public ResponseEntity<String> exportFullDot(
            @RequestParam(required = false) List<String> types
    ) {
        try {
            List<EntryPointType> entryPointTypes = types != null ? 
                    types.stream().map(EntryPointType::valueOf).toList() : List.of();
            String dotContent = graphExportService.exportFullGraphToDot(entryPointTypes);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/vnd.graphviz")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"impact-graph-full.dot\"")
                    .body(dotContent);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Export failed: " + e.getMessage(), e);
        }
    }

    /**
     * Export impact graph to JSON format (for D3.js visualization).
     */
    @GetMapping("/export/json")
    public ResponseEntity<String> exportJson(
            @RequestParam String entryPointId,
            @RequestParam(defaultValue = "5") int depth
    ) {
        try {
            String jsonContent = graphExportService.exportToJson(entryPointId, depth);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + entryPointId + ".json\"")
                    .body(jsonContent);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Export failed: " + e.getMessage(), e);
        }
    }

    /**
     * Export impact graph to Mermaid format (for documentation).
     */
    @GetMapping("/export/mermaid")
    public ResponseEntity<String> exportMermaid(
            @RequestParam String entryPointId,
            @RequestParam(defaultValue = "5") int depth
    ) {
        String mermaidContent = graphExportService.exportToMermaid(entryPointId, depth);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/plain")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + entryPointId + ".mmd\"")
                .body(mermaidContent);
    }

    /**
     * Export impact graph to PlantUML format.
     */
    @GetMapping("/export/plantuml")
    public ResponseEntity<String> exportPlantUml(
            @RequestParam String entryPointId,
            @RequestParam(defaultValue = "5") int depth
    ) {
        String plantumlContent = graphExportService.exportToPlantUml(entryPointId, depth);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/plain")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + entryPointId + ".puml\"")
                .body(plantumlContent);
    }
}
