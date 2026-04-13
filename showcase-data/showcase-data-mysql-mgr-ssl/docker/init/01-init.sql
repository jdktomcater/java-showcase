SET @@SESSION.SQL_LOG_BIN = 0;

CREATE DATABASE IF NOT EXISTS mgr_demo;

CREATE TABLE IF NOT EXISTS mgr_demo.ssl_probe (
    id BIGINT PRIMARY KEY,
    label VARCHAR(64) NOT NULL,
    detail VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO mgr_demo.ssl_probe (id, label, detail)
VALUES (1, 'ssl-check', 'This row is used by the local validation service')
ON DUPLICATE KEY UPDATE detail = VALUES(detail);

CREATE USER IF NOT EXISTS 'repl'@'%' IDENTIFIED BY 'Repl#123456' REQUIRE SSL;
GRANT REPLICATION SLAVE, CONNECTION_ADMIN, BACKUP_ADMIN ON *.* TO 'repl'@'%';

CREATE USER IF NOT EXISTS 'app_user'@'%' IDENTIFIED BY 'App#123456' REQUIRE SSL;
GRANT SELECT ON mgr_demo.* TO 'app_user'@'%';
GRANT SELECT ON performance_schema.* TO 'app_user'@'%';
GRANT REPLICATION CLIENT ON *.* TO 'app_user'@'%';

FLUSH PRIVILEGES;

SET @@SESSION.SQL_LOG_BIN = 1;
