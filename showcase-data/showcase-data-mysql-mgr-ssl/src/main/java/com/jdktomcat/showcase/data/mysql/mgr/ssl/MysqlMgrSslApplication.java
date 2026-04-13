package com.jdktomcat.showcase.data.mysql.mgr.ssl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@MapperScan("com.jdktomcat.showcase.data.mysql.mgr.ssl.mapper")
@ConfigurationPropertiesScan
@SpringBootApplication
public class MysqlMgrSslApplication {

    public static void main(String[] args) {
        SpringApplication.run(MysqlMgrSslApplication.class, args);
    }
}
