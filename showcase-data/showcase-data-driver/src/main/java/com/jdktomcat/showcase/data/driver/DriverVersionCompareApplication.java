package com.jdktomcat.showcase.data.driver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.jdktomcat.showcase.data.driver.config.BenchmarkProperties;

@SpringBootApplication
@MapperScan("com.jdktomcat.showcase.data.driver.mapper")
@EnableConfigurationProperties(BenchmarkProperties.class)
public class DriverVersionCompareApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriverVersionCompareApplication.class, args);
    }
}
