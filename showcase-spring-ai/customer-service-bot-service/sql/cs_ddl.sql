-- Core tables for Telegram-based CS system

CREATE TABLE IF NOT EXISTS telegram_user_binding (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  telegram_chat_id VARCHAR(64) NOT NULL,
  telegram_user_id VARCHAR(64) NULL,
  platform_user_id BIGINT NULL,
  merchant_id BIGINT NULL,
  bind_status TINYINT NOT NULL DEFAULT 0, -- 0=pending,1=bound,2=revoked
  bind_time DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_telegram_chat (telegram_chat_id),
  KEY idx_platform_user (platform_user_id),
  KEY idx_merchant (merchant_id)
);

CREATE TABLE IF NOT EXISTS cs_session (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id VARCHAR(64) NOT NULL,
  telegram_chat_id VARCHAR(64) NOT NULL,
  platform_user_id BIGINT NULL,
  lang VARCHAR(16) NULL,
  state VARCHAR(32) NULL,
  last_active_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_session (session_id),
  KEY idx_chat_last_active (telegram_chat_id, last_active_at)
);

CREATE TABLE IF NOT EXISTS cs_ticket (
  ticket_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_user_id BIGINT NULL,
  merchant_id BIGINT NULL,
  channel VARCHAR(16) NOT NULL DEFAULT 'telegram',
  status TINYINT NOT NULL DEFAULT 0, -- 0=open,1=assigned,2=resolved,3=closed
  priority TINYINT NOT NULL DEFAULT 0,
  intent VARCHAR(64) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  closed_at DATETIME NULL,
  KEY idx_user_status_created (platform_user_id, status, created_at),
  KEY idx_merchant_status_created (merchant_id, status, created_at)
);

CREATE TABLE IF NOT EXISTS cs_message (
  msg_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id VARCHAR(64) NOT NULL,
  role VARCHAR(16) NOT NULL, -- user/assistant/agent
  content TEXT NOT NULL,
  tokens INT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_session_created (session_id, created_at)
);

-- Optional KB tables (for RAG)
CREATE TABLE IF NOT EXISTS kb_document (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  source VARCHAR(64) NOT NULL,
  title VARCHAR(256) NULL,
  lang VARCHAR(16) NULL,
  tags VARCHAR(256) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS kb_chunk (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  document_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  lang VARCHAR(16) NULL,
  tags VARCHAR(256) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_doc (document_id),
  CONSTRAINT fk_kb_chunk_doc FOREIGN KEY (document_id) REFERENCES kb_document(id)
);

-- Embedding storage (implementation-specific). Using VARBINARY for portability.
CREATE TABLE IF NOT EXISTS kb_chunk_embedding (
  chunk_id BIGINT PRIMARY KEY,
  embedding VARBINARY(8192) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_kb_chunk_emb FOREIGN KEY (chunk_id) REFERENCES kb_chunk(id)
);



