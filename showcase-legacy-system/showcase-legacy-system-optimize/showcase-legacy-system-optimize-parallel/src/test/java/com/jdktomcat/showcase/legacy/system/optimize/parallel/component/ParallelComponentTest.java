package com.jdktomcat.showcase.legacy.system.optimize.parallel.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通用 ParallelComponent 单元测试
 */
@DisplayName("ParallelComponent 通用组件")
class ParallelComponentTest {

    private ParallelComponent component;

    @BeforeEach
    void setUp() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        component = new ParallelComponent(executor,null);
    }

    @Test
    @DisplayName("串行与并行结果一致")
    void serialAndParallelProduceSameResults() {
        List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);

        List<String> serial = component.executeSerial(input, i -> "value-" + i);
        List<String> parallel = component.executeParallel(input, i -> "value-" + i);

        assertEquals(serial, parallel);
        assertEquals(Arrays.asList("value-1", "value-2", "value-3", "value-4", "value-5"), serial);
    }

    @Test
    @DisplayName("结果顺序与输入一致")
    void resultOrderMatchesInput() {
        List<String> input = Arrays.asList("c", "a", "b");

        List<String> serial = component.executeSerial(input, String::toUpperCase);
        List<String> parallel = component.executeParallel(input, String::toUpperCase);

        assertEquals(Arrays.asList("C", "A", "B"), serial);
        assertEquals(Arrays.asList("C", "A", "B"), parallel);
    }

    @Test
    @DisplayName("支持不同类型转换")
    void supportsDifferentTypes() {
        List<Integer> ids = Arrays.asList(100, 200, 300);

        List<Long> serial = component.executeSerial(ids, id -> (long) id * 2);
        List<Long> parallel = component.executeParallel(ids, id -> (long) id * 2);

        assertEquals(Arrays.asList(200L, 400L, 600L), serial);
        assertEquals(serial, parallel);
    }
}
