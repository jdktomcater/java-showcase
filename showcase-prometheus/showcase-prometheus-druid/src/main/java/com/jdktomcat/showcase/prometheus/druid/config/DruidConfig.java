package com.jdktomcat.showcase.prometheus.druid.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceBuilder;
import com.jdktomcat.showcase.prometheus.druid.config.properties.OrderDruidProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * druid 配置多数据源
 *
 * @author yuhata
 */
@Configuration
public class DruidConfig {

    @Bean
    public DataSource orderDataSource(OrderDruidProperties orderDruidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return orderDruidProperties.dataSource(dataSource);
    }
}
