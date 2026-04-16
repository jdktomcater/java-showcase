package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

public class MybatisPlusProbeResult {

    private final boolean valid;
    private final String routedHost;
    private final Long probeId;
    private final String label;
    private final String detailBefore;
    private final String detailAfter;
    private final Integer updatedRows;
    private final String errorMessage;

    public MybatisPlusProbeResult(
            boolean valid,
            String routedHost,
            Long probeId,
            String label,
            String detailBefore,
            String detailAfter,
            Integer updatedRows,
            String errorMessage
    ) {
        this.valid = valid;
        this.routedHost = routedHost;
        this.probeId = probeId;
        this.label = label;
        this.detailBefore = detailBefore;
        this.detailAfter = detailAfter;
        this.updatedRows = updatedRows;
        this.errorMessage = errorMessage;
    }

    public boolean isValid() {
        return valid;
    }

    public String getRoutedHost() {
        return routedHost;
    }

    public Long getProbeId() {
        return probeId;
    }

    public String getLabel() {
        return label;
    }

    public String getDetailBefore() {
        return detailBefore;
    }

    public String getDetailAfter() {
        return detailAfter;
    }

    public Integer getUpdatedRows() {
        return updatedRows;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
