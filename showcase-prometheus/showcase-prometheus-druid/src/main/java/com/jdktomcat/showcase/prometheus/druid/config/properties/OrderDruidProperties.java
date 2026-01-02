package com.jdktomcat.showcase.prometheus.druid.config.properties;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * druid 配置属性
 *
 * @author js-oswald
 */
@Configuration
public class OrderDruidProperties extends  DruidProperties {

    @Value(("${spring.datasource.druid.order.password}"))
    private String password;

    @Value(("${spring.datasource.druid.order.username}"))
    private String username;

    @Value(("${spring.datasource.druid.order.url}"))
    private String url;

    /**
     * 配置
     *
     * @param datasource 数据源
     * @return 数据源
     */
    public DataSource dataSource(DruidDataSource datasource) {
        super.dataSource(datasource);
        datasource.setUsername(username);
        datasource.setUrl(url);
        datasource.setPassword(password);
        return datasource;
    }
}
