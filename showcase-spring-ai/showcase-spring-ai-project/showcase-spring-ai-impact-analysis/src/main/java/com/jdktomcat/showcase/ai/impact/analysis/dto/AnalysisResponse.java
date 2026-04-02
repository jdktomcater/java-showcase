package com.jdktomcat.showcase.ai.impact.analysis.dto;

/**
 * Response for analysis execution.
 */
public record AnalysisResponse(
        boolean success,
        int entryPointCount,
        int nodeCount,
        int relationCount,
        String message
) {
}
