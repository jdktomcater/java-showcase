package com.jdktomcat.showcase.data.mysql.mgr.ssl.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.config.MysqlMgrDataSourceFactory;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.config.MysqlMgrSslProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MysqlMgrEndpointJdbcService implements DisposableBean {

    private final MysqlMgrDataSourceFactory dataSourceFactory;
    private final Map<String, DruidDataSource> dataSources = new ConcurrentHashMap<>();
    private final Map<String, JdbcTemplate> jdbcTemplates = new ConcurrentHashMap<>();

    public MysqlMgrEndpointJdbcService(MysqlMgrDataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public JdbcTemplate jdbcTemplateForNode(MysqlMgrSslProperties.Node node) {
        String key = String.format("node:%s:%d/%s", node.getHost(), node.getPort(), node.getDatabase());
        return jdbcTemplates.computeIfAbsent(key, ignored -> {
            DruidDataSource dataSource = dataSourceFactory.createDataSource(node.getHost(), node.getPort(), node.getDatabase());
            dataSources.put(key, dataSource);
            return new JdbcTemplate(dataSource);
        });
    }

    public JdbcTemplate jdbcTemplateForEndpoint(MysqlMgrSslProperties.Endpoint endpoint) {
        String key = String.format("endpoint:%s:%d/%s", endpoint.getHost(), endpoint.getPort(), endpoint.getDatabase());
        return jdbcTemplates.computeIfAbsent(key, ignored -> {
            DruidDataSource dataSource = dataSourceFactory.createDataSource(endpoint.getHost(), endpoint.getPort(), endpoint.getDatabase());
            dataSources.put(key, dataSource);
            return new JdbcTemplate(dataSource);
        });
    }

    @Override
    public void destroy() {
        dataSources.values().forEach(DruidDataSource::close);
    }
}
