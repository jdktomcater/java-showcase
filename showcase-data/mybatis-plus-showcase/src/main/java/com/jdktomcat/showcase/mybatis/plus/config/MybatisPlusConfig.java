package com.jdktomcat.showcase.mybatis.plus.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis Plus Config
 */
@Configuration
@MapperScan("com.jdktomcat.showcase.mybatis.plus.mapper")
public class MybatisPlusConfig {

}
