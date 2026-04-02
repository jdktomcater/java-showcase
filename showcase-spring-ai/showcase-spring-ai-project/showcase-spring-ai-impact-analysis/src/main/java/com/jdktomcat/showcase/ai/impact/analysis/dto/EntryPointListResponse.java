package com.jdktomcat.showcase.ai.impact.analysis.dto;

import java.util.List;
import java.util.Map;

/**
 * Response for entry point list query.
 */
public record EntryPointListResponse(
        boolean success,
        int count,
        List<Map<String, Object>> entryPoints
) {
}
