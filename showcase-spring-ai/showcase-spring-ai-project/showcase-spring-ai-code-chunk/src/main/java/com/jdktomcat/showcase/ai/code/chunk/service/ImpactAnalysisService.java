package com.jdktomcat.showcase.ai.code.chunk.service;

import com.jdktomcat.showcase.ai.code.chunk.dto.GraphViewResponse;
import com.jdktomcat.showcase.ai.code.chunk.repository.Neo4jGraphRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImpactAnalysisService {

    private final Neo4jGraphRepository graphRepository;

    public List<Map<String, Object>> dependencies(String nodeType, String fqn, int depth) {
        return graphRepository.findDependencies(nodeType.toUpperCase() + ":" + fqn, depth);
    }

    public List<Map<String, Object>> impact(String methodFqn, int depth) {
        return graphRepository.findImpact("METHOD:" + methodFqn, depth);
    }

    public GraphViewResponse dependencyGraph(String nodeType, String fqn, int depth) {
        String nodeId = nodeType.toUpperCase() + ":" + fqn;
        Neo4jGraphRepository.GraphData graphData = graphRepository.findDependencyGraph(nodeId, depth);
        return new GraphViewResponse(
                true,
                "dependencies",
                nodeId,
                depth,
                graphData.nodes().size(),
                graphData.edges().size(),
                graphData.nodes(),
                graphData.edges()
        );
    }

    public GraphViewResponse impactGraph(String methodFqn, int depth) {
        String methodId = "METHOD:" + methodFqn;
        Neo4jGraphRepository.GraphData graphData = graphRepository.findImpactGraph(methodId, depth);
        return new GraphViewResponse(
                true,
                "impact",
                methodId,
                depth,
                graphData.nodes().size(),
                graphData.edges().size(),
                graphData.nodes(),
                graphData.edges()
        );
    }

    public GraphViewResponse fullTypeDependencyGraph() {
        Neo4jGraphRepository.GraphData graphData = graphRepository.findFullTypeDependencyGraph();
        return new GraphViewResponse(
                true,
                "classgraph",
                "TYPE:*",
                0,
                graphData.nodes().size(),
                graphData.edges().size(),
                graphData.nodes(),
                graphData.edges()
        );
    }
}
