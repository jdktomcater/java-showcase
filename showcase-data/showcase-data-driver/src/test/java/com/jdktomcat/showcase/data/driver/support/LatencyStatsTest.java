package com.jdktomcat.showcase.data.driver.support;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LatencyStatsTest {

    @Test
    void shouldCalculatePercentilesAndQps() {
        LatencyStats latencyStats = LatencyStats.fromNanos(Arrays.asList(
                1_000_000L,
                2_000_000L,
                3_000_000L,
                4_000_000L,
                5_000_000L));

        Assertions.assertEquals(new BigDecimal("3.000"), latencyStats.getAvgMillis());
        Assertions.assertEquals(new BigDecimal("5.000"), latencyStats.getP95Millis());
        Assertions.assertEquals(new BigDecimal("5.000"), latencyStats.getMaxMillis());
        Assertions.assertEquals(new BigDecimal("333.33"), latencyStats.getQps());
    }
}
