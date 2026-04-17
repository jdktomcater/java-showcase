package com.jdktomcat.showcase.data.druid;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DruidSimpleDemo {

    private static final String DEFAULT_MYSQL_URL = "jdbc:mysql://127.0.0.1:3306/showcase_druid"
            + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai"
            + "&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true";
    private static final String DEFAULT_MYSQL_USERNAME = "root";
    private static final String DEFAULT_MYSQL_PASSWORD = "yuhata2308";

    public static void main(String[] args) throws SQLException {
        List<String> userNames = querySampleUsers();
        System.out.println("Druid demo query result: " + userNames);
    }

    public static List<String> querySampleUsers() throws SQLException {
        try (DruidDataSource dataSource = createDataSourceFromEnv()) {
            createTable(dataSource);
            clearTable(dataSource);
            insertUsers(dataSource, Arrays.asList("Alice", "Bob"));
            return queryAllUserNames(dataSource);
        }
    }

    static DruidDataSource createDataSourceFromEnv() {
        String url = readConfig("DRUID_DEMO_MYSQL_URL", "druid.demo.mysql.url", DEFAULT_MYSQL_URL);
        String username = readConfig("DRUID_DEMO_MYSQL_USERNAME", "druid.demo.mysql.username", DEFAULT_MYSQL_USERNAME);
        String password = readConfig("DRUID_DEMO_MYSQL_PASSWORD", "druid.demo.mysql.password", DEFAULT_MYSQL_PASSWORD);
        return createDataSource(url, username, password);
    }

    private static DruidDataSource createDataSource(String url, String username, String password) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(5);
        dataSource.setMinIdle(1);
        dataSource.setValidationQuery("SELECT 1");
        return dataSource;
    }

    private static String readConfig(String envKey, String propertyKey, String defaultValue) {
        String propertyValue = System.getProperty(propertyKey);
        if (hasText(propertyValue)) {
            return propertyValue;
        }
        String envValue = System.getenv(envKey);
        if (hasText(envValue)) {
            return envValue;
        }
        return defaultValue;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static void createTable(DruidDataSource dataSource) throws SQLException {
        String createSql = "CREATE TABLE IF NOT EXISTS t_user ("
                + "id BIGINT PRIMARY KEY AUTO_INCREMENT, "
                + "name VARCHAR(64) NOT NULL"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createSql);
        }
    }

    private static void clearTable(DruidDataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM t_user");
        }
    }

    private static void insertUsers(DruidDataSource dataSource, List<String> userNames) throws SQLException {
        String insertSql = "INSERT INTO t_user(name) VALUES (?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            for (String userName : userNames) {
                preparedStatement.setString(1, userName);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    private static List<String> queryAllUserNames(DruidDataSource dataSource) throws SQLException {
        String querySql = "SELECT name FROM t_user ORDER BY id";
        List<String> userNames = new ArrayList<String>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(querySql)) {
            while (resultSet.next()) {
                userNames.add(resultSet.getString("name"));
            }
        }
        return userNames;
    }
}
