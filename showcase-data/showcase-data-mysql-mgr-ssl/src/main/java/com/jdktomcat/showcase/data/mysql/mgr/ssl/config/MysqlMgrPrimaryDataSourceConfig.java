package com.jdktomcat.showcase.data.mysql.mgr.ssl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MysqlMgrPrimaryDataSourceConfig {

    @Primary
    @Bean
    public DataSource dataSource(MysqlMgrDataSourceFactory dataSourceFactory, MysqlMgrSslProperties properties) {
        MysqlMgrSslProperties.Endpoint endpoint = properties.getProxy().getReadWrite();
        return dataSourceFactory.createDataSource(endpoint.getHost(), endpoint.getPort(), endpoint.getDatabase());
    }

    @Primary
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
