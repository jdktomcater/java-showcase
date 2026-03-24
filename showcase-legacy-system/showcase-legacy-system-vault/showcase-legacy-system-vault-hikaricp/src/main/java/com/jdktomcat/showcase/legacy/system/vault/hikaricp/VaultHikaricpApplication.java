package com.jdktomcat.showcase.legacy.system.vault.hikaricp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 */
@MapperScan(value = "com.jdktomcat.showcase.legacy.system.vault.hikaricp.mapper")
@SpringBootApplication
public class VaultHikaricpApplication {
    public static void main(String[] args) {
        SpringApplication.run(VaultHikaricpApplication.class, args);
    }
}
