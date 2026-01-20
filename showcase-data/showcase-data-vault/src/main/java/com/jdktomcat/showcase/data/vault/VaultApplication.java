package com.jdktomcat.showcase.data.vault;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * vault application entrance
 */
@MapperScan(value = "com.jdktomcat.showcase.data.vault.mapper")
@SpringBootApplication
public class VaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaultApplication.class, args);
    }
}
