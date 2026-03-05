package com.jdktomcat.showcase.legacy.system.vault.ob;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 */
@MapperScan(value = "com.jdktomcat.showcase.legacy.system.vault.ob.mapper")
@SpringBootApplication
public class LegacySystemVaultObApplication {
    public static void main(String[] args) {
        SpringApplication.run(LegacySystemVaultObApplication.class, args);
    }
}
