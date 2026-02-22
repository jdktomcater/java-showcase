package com.jdktomcat.showcase.legacy.system.vault.verify.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * druid 配置多数据源
 *
 * @author js-oswald
 */
@Configuration
public class DruidConfig {

    @Bean
    public DruidDataSource slaveTypayv2DataSource(TyPayV2SlaveDruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean
    public SwitchableDataSource slaveTypayv2DataSourceWrapper(DruidDataSource slaveTypayv2DataSource) {
        return new SwitchableDataSource(slaveTypayv2DataSource);
    }
}
