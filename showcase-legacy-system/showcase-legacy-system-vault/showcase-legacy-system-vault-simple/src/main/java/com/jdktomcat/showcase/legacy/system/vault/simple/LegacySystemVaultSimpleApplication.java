package com.jdktomcat.showcase.legacy.system.vault.simple;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hello world!
 */
@MapperScan(value = "com.jdktomcat.showcase.legacy.system.vault.simple.mapper")
@SpringBootApplication
@EnableScheduling
public class LegacySystemVaultSimpleApplication {
    public static void main(String[] args) {
        SpringApplication.run(LegacySystemVaultSimpleApplication.class, args);
    }
}
