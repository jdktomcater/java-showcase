package com.jdktomcat.showcase.data.driver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.benchmark")
public class BenchmarkProperties {

    private boolean enabled = true;

    private int seedSize = 5000;

    private int batchSize = 500;

    private int warmupRounds = 80;

    private int measureRounds = 300;

    private int pageSize = 50;
}
