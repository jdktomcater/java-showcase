package com.jdktomcat.showcase.legacy.system.vault.ob.hikaricp.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Registers a switchable wrapper so the rest of the application always talks to
 * the same logical DataSource while the underlying Hikari pool can be replaced.
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "initialHikariDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource initialHikariDataSource(DataSourceProperties properties, OceanBaseUsernameFormatter usernameFormatter) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        dataSource.setUsername(usernameFormatter.buildOceanBaseUsername(properties.getUsername()));
        return dataSource;
    }

    @Bean(name = "dataSource")
    @Primary
    public SwitchableDataSource dataSource(HikariDataSource initialHikariDataSource) {
        try (Connection connection = initialHikariDataSource.getConnection()) {
            // 单位为秒；getConnection() 建立连接时已经进行账号密码认证
            if (!connection.isValid(2)) {
                throw new SQLException("数据库连接有效性校验失败");
            }
        } catch (SQLException | RuntimeException e) {
            throw new IllegalStateException("启动数据库连接校验失败，请检查账号密码、数据库地址及网络", e);
        }
        return new SwitchableDataSource(initialHikariDataSource);
    }
}
