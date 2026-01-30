package com.jdktomcat.showcase.legacy.system.vault.simple.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * druid 配置属性
 *
 * @author js-oswald
 */
@Slf4j
@RefreshScope
@Configuration
public class TyPayV2SlaveDruidProperties extends DruidProperties {

    @Value("${spring.datasource.druid.typayv2slave.password:}")
    private String password;
    @Value("${spring.datasource.druid.typayv2slave.username:}")
    private String username;
    @Value("${spring.datasource.druid.typayv2slave.url:}")
    private String url;

    public DruidDataSource dataSource(DruidDataSource datasource) {
        super.dataSource(datasource);
        datasource.setUsername(username);
        datasource.setUrl(url);
        datasource.setPassword(password);
        // 设置数据源名称
        datasource.setName("typayv2slave");
        return datasource;
    }
}
