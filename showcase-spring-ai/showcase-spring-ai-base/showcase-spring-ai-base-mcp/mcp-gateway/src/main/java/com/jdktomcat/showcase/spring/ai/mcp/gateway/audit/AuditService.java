package com.jdktomcat.showcase.spring.ai.mcp.gateway.audit;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class AuditService {

    private static final Logger AUDIT_LOG = LoggerFactory.getLogger("MCP_AUDIT");

    private final MeterRegistry meterRegistry;

    public AuditService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void record(String principal, String method, String tool, String upstream, boolean allowed,
                       long durationNanos, String error) {
        String outcome = error == null ? "success" : "error";
        Counter.builder("mcp.gateway.requests")
                .tag("principal", valueOrUnknown(principal))
                .tag("method", valueOrUnknown(method))
                .tag("tool", valueOrUnknown(tool))
                .tag("upstream", valueOrUnknown(upstream))
                .tag("outcome", outcome)
                .tag("allowed", Boolean.toString(allowed))
                .register(meterRegistry)
                .increment();
        Timer.builder("mcp.gateway.request.duration")
                .tag("method", valueOrUnknown(method))
                .tag("upstream", valueOrUnknown(upstream))
                .tag("outcome", outcome)
                .register(meterRegistry)
                .record(durationNanos, TimeUnit.NANOSECONDS);
        AUDIT_LOG.info("time={} principal={} method={} tool={} upstream={} allowed={} durationMs={} outcome={} error={}",
                Instant.now(),
                valueOrUnknown(principal),
                valueOrUnknown(method),
                valueOrUnknown(tool),
                valueOrUnknown(upstream),
                allowed,
                TimeUnit.NANOSECONDS.toMillis(durationNanos),
                outcome,
                error == null ? "" : error);
    }

    private String valueOrUnknown(String value) {
        return value == null || value.isBlank() ? "unknown" : value;
    }
}
