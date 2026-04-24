package com.jdktomcat.showcase.data.driver.runner;

import com.jdktomcat.showcase.data.driver.config.BenchmarkProperties;
import com.jdktomcat.showcase.data.driver.domain.IndexVO;
import com.jdktomcat.showcase.data.driver.mapper.IndexMapper;
import com.jdktomcat.showcase.data.driver.service.IndexService;
import com.jdktomcat.showcase.data.driver.service.QueryBenchmarkService;
import com.jdktomcat.showcase.data.driver.support.BenchmarkScenarioResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverVersionComparisonRunner implements ApplicationRunner {

    private final BenchmarkProperties benchmarkProperties;
    private final IndexMapper indexMapper;
    private final IndexService indexService;
    private final QueryBenchmarkService queryBenchmarkService;
    private final DataSource dataSource;
    private final Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!benchmarkProperties.isEnabled()) {
            log.info("MySQL driver benchmark is disabled.");
            return;
        }
        logDriverMetadata();
        List<BenchmarkScenarioResult> scenarioResults = new ArrayList<>();
        scenarioResults.add(queryBenchmarkService.run("succeed", benchmarkProperties.getWarmupRounds(), benchmarkProperties.getMeasureRounds(), (QueryBenchmarkService.QueryTask) iteration -> {
            IndexVO indexVO = indexService.getIndexVOToday();
            IndexVO result = indexMapper.getIndexorderBysucceed(indexVO);
            log.info("getIndexorderBysucceed:{}", result.toString());
        }));
        scenarioResults.add(queryBenchmarkService.run("total", benchmarkProperties.getWarmupRounds(), benchmarkProperties.getMeasureRounds(), (QueryBenchmarkService.QueryTask) iteration -> {
            IndexVO indexVO = indexService.getIndexVOToday();
            Long total = indexMapper.getIndexorderBytotal(indexVO);
            log.info("getIndexorderBytotal:{}", total);
        }));

        scenarioResults.add(queryBenchmarkService.run("fail", benchmarkProperties.getWarmupRounds(), benchmarkProperties.getMeasureRounds(), (QueryBenchmarkService.QueryTask) iteration -> {
            IndexVO indexVO = indexService.getIndexVOToday();
            Long fail = indexMapper.getIndexorderByfail(indexVO);
            log.info("getIndexorderByfail:{}", fail);
        }));

        log.info("Benchmark profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        log.info("Scenario results:");
        for (BenchmarkScenarioResult scenarioResult : scenarioResults) {
            log.info("{} -> avg={} ms, p95={} ms, max={} ms, qps={}", scenarioResult.getScenarioName(), scenarioResult.getLatencyStats().getAvgMillis(), scenarioResult.getLatencyStats().getP95Millis(), scenarioResult.getLatencyStats().getMaxMillis(), scenarioResult.getLatencyStats().getQps());
        }
        log.info("Repeat the same command with a different Maven profile to compare driver impact on query latency.");
    }

    private void logDriverMetadata() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            log.info("Driver metadata -> name={}, version={}, jdbcVersion={}.{}, database={}, databaseVersion={}", metaData.getDriverName(), metaData.getDriverVersion(), metaData.getJDBCMajorVersion(), metaData.getJDBCMinorVersion(), metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
        }
    }
}
