package com.jdktomcat.showcase.legacy.system.vault.verify.health;

import com.jdktomcat.showcase.legacy.system.vault.verify.config.SwitchableDataSource;
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

    private final Map<String, SwitchableDataSource> dataSources;

    public CustomDbHealthIndicator(Map<String, SwitchableDataSource> dataSources) {
        this.dataSources = dataSources;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        if (dataSources == null || dataSources.isEmpty()) {
            builder.unknown().withDetail("message", "No SwitchableDataSource found");
            return;
        }
        Map<String, Object> details = new LinkedHashMap<>();
        boolean allUp = true;
        for (Map.Entry<String, SwitchableDataSource> entry : dataSources.entrySet()) {
            String name = entry.getKey();
            try (Connection ignored = entry.getValue().getConnection()) {
                details.put(name, "UP");
            } catch (Exception ex) {
                allUp = false;
                details.put(name, "DOWN: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        }
        if (allUp) {
            builder.up().withDetails(details);
        } else {
            builder.down().withDetails(details);
        }
    }
}
