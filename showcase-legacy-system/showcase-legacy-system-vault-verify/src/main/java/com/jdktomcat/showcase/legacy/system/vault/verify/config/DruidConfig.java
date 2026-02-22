package com.jdktomcat.showcase.legacy.system.vault.verify.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * druid 配置多数据源
 *
 * @author js-oswald
 */
@Configuration
@EnableConfigurationProperties(MybatisPlusProperties.class)
@MapperScan(value = "com.jdktomcat.showcase.legacy.system.vault.verify.mapper", sqlSessionTemplateRef = "ds2SqlSessionTemplate")
public class DruidConfig {

    @Resource
    private MybatisPlusProperties properties;

    public static final String TYPE_ALIASES_PACKAGE = "com.pay.typay.encrypt.handler.domain";

    public static final String TYPE_HANDLERS_PACKAGE = "com.pay.typay.encrypt.handler.type";

    @Bean
    public DruidDataSource slaveTypayv2DataSource(TyPayV2SlaveDruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean
    public SwitchableDataSource slaveTypayv2DataSourceWrapper(DruidDataSource slaveTypayv2DataSource) {
        return new SwitchableDataSource(slaveTypayv2DataSource);
    }

    // JdbcTemplate第二个tyadmin数据源
    @Bean(name = "ds2JdbcTemplate")
    public JdbcTemplate ds2JdbcTemplate(@Qualifier("slaveTypayv2DataSourceWrapper") SwitchableDataSource slaveTypayv2DataSourceWrapper) {
        return new JdbcTemplate(slaveTypayv2DataSourceWrapper);
    }

    // 主数据源 ds1数据源
    @Bean("ds2SqlSessionFactory")
    public SqlSessionFactory ds1SqlSessionFactory(@Qualifier("slaveTypayv2DataSourceWrapper") SwitchableDataSource slaveTypayv2DataSourceWrapper)
            throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(slaveTypayv2DataSourceWrapper);
        // 20200915
        sqlSessionFactory.setVfs(SpringBootVFS.class);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        sqlSessionFactory.setConfiguration(configuration);
        configuration.setCacheEnabled(false);
        sqlSessionFactory.setPlugins(new PaginationInterceptor());
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            sqlSessionFactory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        } else {
            sqlSessionFactory.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            sqlSessionFactory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        } else {
            sqlSessionFactory.setTypeHandlersPackage(TYPE_HANDLERS_PACKAGE);
        }
        return sqlSessionFactory.getObject();

    }

    @Bean(name = "ds2TransactionManager")
    public DataSourceTransactionManager ds1TransactionManager(@Qualifier("slaveTypayv2DataSourceWrapper") SwitchableDataSource slaveTypayv2DataSourceWrapper) {
        return new DataSourceTransactionManager(slaveTypayv2DataSourceWrapper);
    }

    @Bean(name = "ds2SqlSessionTemplate")
    public SqlSessionTemplate ds1SqlSessionTemplate(@Qualifier("ds2SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
