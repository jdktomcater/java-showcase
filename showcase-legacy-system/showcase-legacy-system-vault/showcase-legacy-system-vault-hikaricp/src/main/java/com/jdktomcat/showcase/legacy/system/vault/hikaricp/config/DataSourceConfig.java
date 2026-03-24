package com.jdktomcat.showcase.legacy.system.vault.hikaricp.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Registers a switchable wrapper so the rest of the application always talks to
 * the same logical DataSource while the underlying Hikari pool can be replaced.
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "initialHikariDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public HikariDataSource initialHikariDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "dataSource")
    @Primary
    public SwitchableDataSource dataSource(HikariDataSource initialHikariDataSource) {
        return new SwitchableDataSource(initialHikariDataSource);
    }
}
