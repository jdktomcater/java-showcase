package com.jdktomcat.showcase.legacy.system.vault;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 */
@MapperScan(value = "com.jdktomcat.showcase.legacy.system.vault.mapper")
@SpringBootApplication
public class LegacySystemVaultApplication {
    public static void main(String[] args) {
        SpringApplication.run(LegacySystemVaultApplication.class, args);
    }
}
