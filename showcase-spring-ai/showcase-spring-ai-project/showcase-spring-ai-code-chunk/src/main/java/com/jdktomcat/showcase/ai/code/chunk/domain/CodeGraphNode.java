package com.jdktomcat.showcase.ai.code.chunk.domain;

public record CodeGraphNode(
        String id,
        NodeType type,
        String name,
        String fqn,
        String filePath,
        String module,
        Integer startLine,
        Integer endLine
) {
}
