package com.jdktomcat.showcase.legacy.system.vault.ob.hikaricp;

import com.jdktomcat.showcase.legacy.system.vault.ob.hikaricp.config.SwitchableDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Two pools share one isolated database, just like credential rotation.
 * No Spring Boot context: application.yaml and Vault are deliberately not loaded.
 */
class DataSourceSwitchTransactionTest {
    private HikariDataSource oldPool;
    private HikariDataSource newPool;
    private SwitchableDataSource dataSource;
    private JdbcTemplate jdbc;
    private DataSourceTransactionManager manager;
    private final String table = "tx_probe_" + UUID.randomUUID().toString().replace("-", "");

    @BeforeEach
    void setUp() {
        String url = System.getProperty("tx.probe.jdbc-url",
                "jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        oldPool = pool(url, "before-switch");
        newPool = pool(url, "after-switch");
        dataSource = new SwitchableDataSource(oldPool);
        jdbc = new JdbcTemplate(dataSource);
        manager = new DataSourceTransactionManager(dataSource);
        jdbc.execute("create table " + table + " (id int primary key)");
    }

    private HikariDataSource pool(String url, String name) {
        HikariDataSource pool = new HikariDataSource();
        pool.setJdbcUrl(url);
        String prefix = "before-switch".equals(name) ? "tx.probe.old." : "tx.probe.new.";
        pool.setUsername(System.getProperty(prefix + "username", "sa"));
        pool.setPassword(System.getProperty(prefix + "password", ""));
        pool.setPoolName(name);
        pool.setMaximumPoolSize(2);
        pool.setMinimumIdle(0);
        return pool;
    }

    @AfterEach
    void tearDown() {
        if (jdbc != null) {
            try {
                jdbc.execute("drop table if exists " + table);
            } finally {
                if (oldPool != null) oldPool.close();
                if (newPool != null) newPool.close();
            }
        }
        if (oldPool != null) oldPool.close();
        if (newPool != null) newPool.close();
    }

    private TransactionTemplate transaction(int propagation) {
        TransactionTemplate template = new TransactionTemplate(manager);
        template.setPropagationBehavior(propagation);
        return template;
    }

    private Connection boundConnection() {
        // Only used inside a transaction: the transaction manager owns release.
        return DataSourceUtils.getConnection(dataSource);
    }

    private void switchPool() {
        dataSource.switchTo(newPool);
        assertSame(newPool, dataSource.getCurrent());
        // This active connection also prevents the listener from closing the old pool.
        assertEquals(1, oldPool.getHikariPoolMXBean().getActiveConnections());
    }

    private void assertRows(Integer... ids) {
        assertEquals(java.util.Arrays.asList(ids),
                jdbc.queryForList("select id from " + table + " order by id", Integer.class));
    }

    private void insert(int id) {
        jdbc.update("insert into " + table + " values (?)", id);
    }

    @Test
    void nestedRollbackUsesOldConnectionAndOuterCanCommit() {
        transaction(TransactionDefinition.PROPAGATION_REQUIRED).execute(outer -> {
            Connection original = boundConnection();
            insert(1);
            switchPool();
            transaction(TransactionDefinition.PROPAGATION_NESTED).execute(inner -> {
                assertTrue(inner.hasSavepoint());
                assertSame(original, boundConnection());
                insert(2);
                inner.setRollbackOnly();
                return null;
            });
            assertSame(original, boundConnection());
            insert(3);
            return null;
        });
        assertRows(1, 3);
        assertEquals(0, oldPool.getHikariPoolMXBean().getActiveConnections());
        oldPool.close();
        transaction(TransactionDefinition.PROPAGATION_REQUIRED).execute(status -> {
            assertEquals(1, newPool.getHikariPoolMXBean().getActiveConnections());
            insert(4);
            return null;
        });
        assertRows(1, 3, 4);
    }

    @Test
    void nestedCommitIsStillRolledBackWithOuterTransaction() {
        transaction(TransactionDefinition.PROPAGATION_REQUIRED).execute(outer -> {
            Connection original = boundConnection();
            insert(1);
            transaction(TransactionDefinition.PROPAGATION_NESTED).execute(inner -> {
                switchPool(); // Also cover rotation while a savepoint already exists.
                assertTrue(inner.hasSavepoint());
                assertSame(original, boundConnection());
                insert(2);
                return null;
            });
            outer.setRollbackOnly();
            return null;
        });
        assertRows();
    }

    @Test
    void requiredInnerContinuesUsingOuterConnectionAfterSwitch() {
        transaction(TransactionDefinition.PROPAGATION_REQUIRED).execute(outer -> {
            Connection original = boundConnection();
            insert(1);
            switchPool();
            transaction(TransactionDefinition.PROPAGATION_REQUIRED).execute(inner -> {
                assertFalse(inner.isNewTransaction());
                assertSame(original, boundConnection());
                insert(2);
                return null;
            });
            return null;
        });
        assertRows(1, 2);
    }

    @Test
    void requiresNewUsesNewPoolAndOuterResumesOldConnection() {
        transaction(TransactionDefinition.PROPAGATION_REQUIRED).execute(outer -> {
            Connection original = boundConnection();
            insert(1);
            switchPool();
            transaction(TransactionDefinition.PROPAGATION_REQUIRES_NEW).execute(inner -> {
                assertTrue(inner.isNewTransaction());
                assertNotSame(original, boundConnection());
                assertEquals(1, oldPool.getHikariPoolMXBean().getActiveConnections());
                assertEquals(1, newPool.getHikariPoolMXBean().getActiveConnections());
                insert(2);
                return null;
            });
            assertSame(original, boundConnection());
            insert(3);
            outer.setRollbackOnly();
            return null;
        });
        assertRows(2); // Independent inner commit survives the outer rollback.
    }
}
