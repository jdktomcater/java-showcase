package com.jdktomcat.showcase.data.driver.support;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LatencyStats {

    private BigDecimal avgMillis;

    private BigDecimal p95Millis;

    private BigDecimal maxMillis;

    private BigDecimal qps;

    public static LatencyStats fromNanos(List<Long> nanosSamples) {
        if (nanosSamples == null || nanosSamples.isEmpty()) {
            return new LatencyStats(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        List<Long> sortedSamples = new ArrayList<Long>(nanosSamples);
        Collections.sort(sortedSamples);

        long totalNanos = 0L;
        long maxNanos = 0L;
        for (Long sample : sortedSamples) {
            totalNanos += sample;
            if (sample > maxNanos) {
                maxNanos = sample;
            }
        }

        int p95Index = (int) Math.ceil(sortedSamples.size() * 0.95D) - 1;
        if (p95Index < 0) {
            p95Index = 0;
        }

        BigDecimal totalNanosDecimal = BigDecimal.valueOf(totalNanos);
        BigDecimal sampleSizeDecimal = BigDecimal.valueOf(sortedSamples.size());
        BigDecimal avgMillis = totalNanosDecimal.divide(sampleSizeDecimal, 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(1_000_000L), 3, RoundingMode.HALF_UP);
        BigDecimal p95Millis = BigDecimal.valueOf(sortedSamples.get(p95Index))
                .divide(BigDecimal.valueOf(1_000_000L), 3, RoundingMode.HALF_UP);
        BigDecimal maxMillis = BigDecimal.valueOf(maxNanos)
                .divide(BigDecimal.valueOf(1_000_000L), 3, RoundingMode.HALF_UP);

        BigDecimal totalSeconds = totalNanosDecimal.divide(BigDecimal.valueOf(1_000_000_000L), 9, RoundingMode.HALF_UP);
        BigDecimal qps = totalSeconds.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : sampleSizeDecimal.divide(totalSeconds, 2, RoundingMode.HALF_UP);
        return new LatencyStats(avgMillis, p95Millis, maxMillis, qps);
    }
}
