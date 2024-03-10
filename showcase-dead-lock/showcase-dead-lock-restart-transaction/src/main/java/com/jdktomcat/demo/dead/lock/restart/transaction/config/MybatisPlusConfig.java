package com.jdktomcat.demo.dead.lock.restart.transaction.config;

import com.jdktomcat.demo.dead.lock.restart.transaction.config.mybatis.DeadLockInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis Plus Config
 */
@Configuration
@MapperScan("com.jdktomcat.demo.dead.lock.restart.transaction.mapper")
public class MybatisPlusConfig {

    @Bean
    public String deadLockInterceptor(SqlSessionFactory sqlSessionFactory) {
        //实例化插件
        DeadLockInterceptor deadLockInterceptor = new DeadLockInterceptor();
        //将插件添加到SqlSessionFactory工厂
        sqlSessionFactory.getConfiguration().addInterceptor(deadLockInterceptor);
        return "deadLockInterceptor";
    }
}
