package com.jdktomcat.showcase.ai.code.chunk.service;

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
}
