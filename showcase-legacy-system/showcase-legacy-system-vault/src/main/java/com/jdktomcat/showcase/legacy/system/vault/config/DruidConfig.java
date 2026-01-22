package com.jdktomcat.showcase.legacy.system.vault.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * druid 配置多数据源
 *
 * @author js-oswald
 */
@Configuration
public class DruidConfig {

    @Bean
    public DataSource slaveTypayv2DataSource(TyPayV2SlaveDruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }
}
