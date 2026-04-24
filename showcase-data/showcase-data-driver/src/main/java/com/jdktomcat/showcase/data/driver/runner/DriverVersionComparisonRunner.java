package com.jdktomcat.showcase.data.driver.runner;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.jdktomcat.showcase.data.driver.config.BenchmarkProperties;
import com.jdktomcat.showcase.data.driver.mapper.DriverBenchmarkMapper;
import com.jdktomcat.showcase.data.driver.service.BenchmarkDataInitializer;
import com.jdktomcat.showcase.data.driver.service.QueryBenchmarkService;
import com.jdktomcat.showcase.data.driver.support.BenchmarkScenarioResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverVersionComparisonRunner implements ApplicationRunner {

    private final BenchmarkProperties benchmarkProperties;
    private final BenchmarkDataInitializer benchmarkDataInitializer;
    private final DriverBenchmarkMapper driverBenchmarkMapper;
    private final QueryBenchmarkService queryBenchmarkService;
    private final DataSource dataSource;
    private final Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!benchmarkProperties.isEnabled()) {
            log.info("MySQL driver benchmark is disabled.");
            return;
        }

        benchmarkDataInitializer.initialize();
        logDriverMetadata();

        int seedSize = benchmarkProperties.getSeedSize();
        int pageSize = benchmarkProperties.getPageSize();
        String[] statuses = BenchmarkDataInitializer.STATUSES;
        LocalDateTime baseTime = BenchmarkDataInitializer.BASE_CREATED_AT;
        List<BenchmarkScenarioResult> scenarioResults = new ArrayList<BenchmarkScenarioResult>();

        scenarioResults.add(queryBenchmarkService.run("point-select-by-id",
                benchmarkProperties.getWarmupRounds(),
                benchmarkProperties.getMeasureRounds(),
                new QueryBenchmarkService.QueryTask() {
                    @Override
                    public void run(int iteration) {
                        long id = iteration % seedSize + 1L;
                        driverBenchmarkMapper.selectById(id);
                    }
                }));

        scenarioResults.add(queryBenchmarkService.run("status-page-query",
                benchmarkProperties.getWarmupRounds(),
                benchmarkProperties.getMeasureRounds(),
                new QueryBenchmarkService.QueryTask() {
                    @Override
                    public void run(int iteration) {
                        String status = statuses[iteration % statuses.length];
                        driverBenchmarkMapper.selectByStatus(status, pageSize);
                    }
                }));

        scenarioResults.add(queryBenchmarkService.run("created-at-window-scan",
                benchmarkProperties.getWarmupRounds(),
                benchmarkProperties.getMeasureRounds(),
                new QueryBenchmarkService.QueryTask() {
                    @Override
                    public void run(int iteration) {
                        int offset = (iteration * 7) % seedSize;
                        LocalDateTime start = baseTime.plusSeconds(offset);
                        LocalDateTime end = start.plusSeconds(120);
                        driverBenchmarkMapper.scanIdsByCreatedAt(start, end, pageSize);
                    }
                }));

        scenarioResults.add(queryBenchmarkService.run("status-amount-aggregate",
                benchmarkProperties.getWarmupRounds(),
                benchmarkProperties.getMeasureRounds(),
                new QueryBenchmarkService.QueryTask() {
                    @Override
                    public void run(int iteration) {
                        String status = statuses[iteration % statuses.length];
                        driverBenchmarkMapper.sumAmountByStatus(status);
                    }
                }));

        log.info("Benchmark dataset rows: {}", driverBenchmarkMapper.countAll());
        log.info("Benchmark profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        log.info("Scenario results:");
        for (BenchmarkScenarioResult scenarioResult : scenarioResults) {
            log.info("{} -> avg={} ms, p95={} ms, max={} ms, qps={}",
                    scenarioResult.getScenarioName(),
                    scenarioResult.getLatencyStats().getAvgMillis(),
                    scenarioResult.getLatencyStats().getP95Millis(),
                    scenarioResult.getLatencyStats().getMaxMillis(),
                    scenarioResult.getLatencyStats().getQps());
        }
        log.info("Repeat the same command with a different Maven profile to compare driver impact on query latency.");
    }

    private void logDriverMetadata() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            log.info("Driver metadata -> name={}, version={}, jdbcVersion={}.{}, database={}, databaseVersion={}",
                    metaData.getDriverName(),
                    metaData.getDriverVersion(),
                    metaData.getJDBCMajorVersion(),
                    metaData.getJDBCMinorVersion(),
                    metaData.getDatabaseProductName(),
                    metaData.getDatabaseProductVersion());
        }
    }
}
