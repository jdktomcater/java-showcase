package com.jdktomcat.showcase.data.driver.support;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BenchmarkScenarioResult {

    private String scenarioName;

    private LatencyStats latencyStats;
}
