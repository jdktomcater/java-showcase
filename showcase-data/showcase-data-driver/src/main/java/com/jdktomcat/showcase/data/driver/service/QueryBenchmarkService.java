package com.jdktomcat.showcase.data.driver.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jdktomcat.showcase.data.driver.support.BenchmarkScenarioResult;
import com.jdktomcat.showcase.data.driver.support.LatencyStats;

@Service
public class QueryBenchmarkService {

    public BenchmarkScenarioResult run(String scenarioName,
                                       int warmupRounds,
                                       int measureRounds,
                                       QueryTask queryTask) throws Exception {
        for (int i = 0; i < warmupRounds; i++) {
            queryTask.run(i);
        }

        List<Long> samples = new ArrayList<Long>(measureRounds);
        for (int i = 0; i < measureRounds; i++) {
            long startNanos = System.nanoTime();
            queryTask.run(i);
            samples.add(System.nanoTime() - startNanos);
        }
        return new BenchmarkScenarioResult(scenarioName, LatencyStats.fromNanos(samples));
    }

    public interface QueryTask {

        void run(int iteration) throws Exception;
    }
}
