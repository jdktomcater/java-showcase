package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

public record MybatisPlusProbeResult(
        boolean valid,
        String routedHost,
        Long probeId,
        String label,
        String detailBefore,
        String detailAfter,
        Integer updatedRows,
        String errorMessage
) {
}
