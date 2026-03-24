package com.jdktomcat.showcase.legacy.system.vault.hikaricp.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

@Slf4j
@Getter
public class SwitchableDataSource extends AbstractDataSource {

    private volatile HikariDataSource current;

    public SwitchableDataSource(HikariDataSource initial) {
        this.current = initial;
    }

    public synchronized void switchTo(HikariDataSource newDs) {
        this.current = newDs;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return current.getConnection();
    }

    /**
     * <p>Attempts to establish a connection with the data source that
     * this {@code DataSource} object represents.
     *
     * @param username the database user on whose behalf the connection is
     *                 being made
     * @param password the user's password
     * @return a connection to the data source
     * @throws SQLException        if a database access error occurs
     * @throws SQLTimeoutException when the driver has determined that the
     *                             timeout value specified by the {@code setLoginTimeout} method
     *                             has been exceeded and has at least tried to cancel the
     *                             current database connection attempt
     * @since 1.4
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return current.getConnection();
    }

    @PreDestroy
    public synchronized void shutdown() {
        log.info("触发优雅下线: SwitchableDataSource shutdown!");
        if (current != null && !current.isClosed()) {
            current.close();
        }
    }
}
