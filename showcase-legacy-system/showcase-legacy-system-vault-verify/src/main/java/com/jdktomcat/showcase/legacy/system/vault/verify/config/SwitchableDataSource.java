package com.jdktomcat.showcase.legacy.system.vault.verify.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

@Getter
public class SwitchableDataSource extends AbstractDataSource {

    private volatile DruidDataSource current;

    public SwitchableDataSource(DruidDataSource initial) {
        this.current = initial;
    }

    public synchronized void switchTo(DruidDataSource newDs) {
        this.current = newDs;
    }

    @Override
    public Connection getConnection() throws SQLException {
        // TODO日志补充
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
        if (current != null && !current.isClosed()) {
            current.close();
        }
    }
}
