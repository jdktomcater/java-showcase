package com.jdktomcat.showcase.legacy.system.vault.tidb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 */
@MapperScan(value = "com.jdktomcat.showcase.legacy.system.vault.tidb.mapper")
@SpringBootApplication
public class LegacySystemVaultTidbApplication {
    public static void main(String[] args) {
        SpringApplication.run(LegacySystemVaultTidbApplication.class, args);
    }
}
