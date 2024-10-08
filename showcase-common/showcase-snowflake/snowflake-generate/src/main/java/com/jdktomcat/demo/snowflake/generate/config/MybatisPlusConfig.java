package com.jdktomcat.demo.snowflake.generate.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis Plus Config
 */
@Configuration
@MapperScan("com.jdktomcat.demo.snowflake.generate.mapper")
public class MybatisPlusConfig {

}
