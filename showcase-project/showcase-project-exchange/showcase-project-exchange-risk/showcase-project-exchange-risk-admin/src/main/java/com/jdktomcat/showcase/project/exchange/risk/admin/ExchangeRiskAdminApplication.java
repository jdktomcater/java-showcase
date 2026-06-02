package com.jdktomcat.showcase.project.exchange.risk.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Exchange risk rule management admin backend.
 *
 * <p>Provides CRUD APIs for operators to configure pre-trade risk rules
 * (order size, frequency, price deviation, symbol restrictions, etc.)
 * that downstream matching / order services consume at runtime.
 */
@SpringBootApplication
public class ExchangeRiskAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeRiskAdminApplication.class, args);
    }
}
