package com.jdktomcat.showcase.legacy.system.vault.ob.hikaricp.config;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Override Spring Boot default dbHealthIndicator.
 * Only checks current data sources behind SwitchableDataSource
 * to avoid probing rotated/closed historical pools.
 */
@Component("dbHealthIndicator")
public class CustomDbHealthIndicator extends AbstractHealthIndicator {

    private final SwitchableDataSource dataSourceWrapper;

    public CustomDbHealthIndicator(SwitchableDataSource dataSourceWrapper) {
        this.dataSourceWrapper = dataSourceWrapper;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        Map<String, Object> details = new LinkedHashMap<>();
        try (Connection ignored = dataSourceWrapper.getConnection()) {
            details.put("dataSource", "UP");
            builder.up().withDetails(details);
        } catch (Exception ex) {
            details.put("dataSource", "DOWN: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            builder.down().withDetails(details);
        }
    }
}
