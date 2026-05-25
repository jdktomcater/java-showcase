-- Real-time Risk Control Service — Database Schema
-- Compatible with MySQL 8.0+

CREATE DATABASE IF NOT EXISTS risk_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE risk_db;

-- ── Risk Check Audit Log ──────────────────────────────────────────────────
-- Append-only.  Every risk decision is written here for compliance, forensics,
-- and offline ML feature engineering.  Never updated after insert.
CREATE TABLE IF NOT EXISTS risk_check_record (
    id                BIGINT          NOT NULL AUTO_INCREMENT,
    request_id        VARCHAR(64)     NOT NULL COMMENT 'Caller-supplied idempotency key',
    user_id           BIGINT          NOT NULL,
    user_ip           VARCHAR(64),
    event_type        VARCHAR(32)     NOT NULL COMMENT 'WITHDRAWAL|TRADE|LOGIN|TRANSFER|DEPOSIT',
    asset_code        VARCHAR(16)              COMMENT 'BTC, ETH, USDT …',
    amount            DECIMAL(30, 8)           COMMENT 'Native asset amount',
    amount_usd        DECIMAL(30, 2)           COMMENT 'USD equivalent at check time',
    to_address        VARCHAR(128)             COMMENT 'On-chain destination (withdrawals)',
    risk_level        VARCHAR(16)     NOT NULL COMMENT 'PASS|REVIEW|BLOCK',
    risk_score        SMALLINT        NOT NULL COMMENT '0–100',
    triggered_rules   TEXT                     COMMENT 'JSON array of rule names',
    reason            VARCHAR(512),
    processing_time_ms BIGINT,
    created_at        DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    PRIMARY KEY (id),
    UNIQUE KEY  uq_request_id (request_id),
    KEY         idx_user_id   (user_id),
    KEY         idx_created_at (created_at),
    KEY         idx_user_level (user_id, risk_level, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='Immutable audit trail of every risk check decision';

-- ── Compliance Task Queue ─────────────────────────────────────────────────
-- Created by the Kafka consumer for REVIEW-level events.
-- Worked by compliance officers via the admin UI.
CREATE TABLE IF NOT EXISTS compliance_task (
    id                BIGINT          NOT NULL AUTO_INCREMENT,
    request_id        VARCHAR(64)     NOT NULL,
    user_id           BIGINT          NOT NULL,
    event_type        VARCHAR(32)     NOT NULL,
    risk_score        SMALLINT        NOT NULL,
    triggered_rules   TEXT,
    reason            VARCHAR(512),
    status            VARCHAR(16)     NOT NULL DEFAULT 'OPEN'
                                      COMMENT 'OPEN|IN_REVIEW|APPROVED|REJECTED',
    assignee          VARCHAR(64)              COMMENT 'Compliance officer username',
    resolved_at       DATETIME,
    resolution_note   VARCHAR(1024),
    created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                      ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY  uq_request_id (request_id),
    KEY         idx_user_status (user_id, status),
    KEY         idx_created_at  (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='Manual review queue for REVIEW-level risk events';

-- ── Create application user ───────────────────────────────────────────────
CREATE USER IF NOT EXISTS 'risk'@'%' IDENTIFIED BY '123456';
GRANT SELECT, INSERT, UPDATE, DELETE ON risk_db.* TO 'risk'@'%';
FLUSH PRIVILEGES;
