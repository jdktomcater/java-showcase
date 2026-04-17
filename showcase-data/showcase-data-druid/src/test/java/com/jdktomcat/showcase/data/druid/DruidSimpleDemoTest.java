package com.jdktomcat.showcase.data.druid;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DruidSimpleDemoTest {

    @Test
    void shouldQuerySampleUsersByDruid() throws SQLException {
        assumeTrue(isMysqlTestEnabled(),
                "Enable local MySQL test by setting DRUID_DEMO_MYSQL_TEST_ENABLED=true "
                        + "or -Ddruid.demo.mysql.test.enabled=true");
        List<String> userNames = DruidSimpleDemo.querySampleUsers();
        assertEquals(Arrays.asList("Alice", "Bob"), userNames);
    }

    private boolean isMysqlTestEnabled() {
        String propertyValue = System.getProperty("druid.demo.mysql.test.enabled");
        if (propertyValue != null) {
            return "true".equalsIgnoreCase(propertyValue);
        }
        String envValue = System.getenv("DRUID_DEMO_MYSQL_TEST_ENABLED");
        return "true".equalsIgnoreCase(envValue);
    }
}
