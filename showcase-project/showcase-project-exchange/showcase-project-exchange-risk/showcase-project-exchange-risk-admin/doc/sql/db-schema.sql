-- Exchange Risk Rule Management — Database Schema (MySQL 8.0+)

CREATE DATABASE IF NOT EXISTS exchange_risk DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE exchange_risk;

CREATE TABLE IF NOT EXISTS exchange_risk_rule (
    id                BIGINT          NOT NULL AUTO_INCREMENT,
    rule_code         VARCHAR(64)     NOT NULL COMMENT 'Unique rule identifier',
    rule_name         VARCHAR(128)    NOT NULL,
    rule_type         VARCHAR(32)     NOT NULL COMMENT 'ORDER_SIZE_LIMIT|DAILY_VOLUME_LIMIT|...',
    scope             VARCHAR(32)     NOT NULL COMMENT 'GLOBAL|USER|SYMBOL|USER_SYMBOL',
    target_user_id    VARCHAR(64),
    target_symbol     VARCHAR(32),
    threshold_value   DECIMAL(30, 8),
    threshold_config  TEXT                     COMMENT 'JSON for complex thresholds',
    action            VARCHAR(16)     NOT NULL COMMENT 'REJECT|REVIEW|ALERT',
    priority          INT             NOT NULL COMMENT 'Lower value = higher priority',
    status            VARCHAR(16)     NOT NULL DEFAULT 'ENABLED',
    description       VARCHAR(512),
    effective_from    DATETIME,
    effective_to      DATETIME,
    created_by        VARCHAR(64),
    updated_by        VARCHAR(64),
    created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_rule_code (rule_code),
    KEY idx_rule_type_status (rule_type, status),
    KEY idx_scope_target (scope, target_user_id, target_symbol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='Exchange pre-trade risk rules managed by admin backend';

CREATE TABLE IF NOT EXISTS exchange_risk_rule_change_log (
    id                BIGINT          NOT NULL AUTO_INCREMENT,
    rule_id           BIGINT          NOT NULL,
    rule_code         VARCHAR(64)     NOT NULL,
    change_type       VARCHAR(16)     NOT NULL COMMENT 'CREATE|UPDATE|STATUS|DELETE',
    before_snapshot   TEXT,
    after_snapshot    TEXT,
    operator          VARCHAR(64)     NOT NULL,
    created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    KEY idx_change_rule_id (rule_id),
    KEY idx_change_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='Audit trail for risk rule configuration changes';

CREATE USER IF NOT EXISTS 'exchange_risk'@'%' IDENTIFIED BY '123456';
GRANT SELECT, INSERT, UPDATE, DELETE ON exchange_risk.* TO 'exchange_risk'@'%';
FLUSH PRIVILEGES;
