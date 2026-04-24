CREATE TABLE IF NOT EXISTS t_driver_order (
    id BIGINT NOT NULL,
    user_name VARCHAR(64) NOT NULL,
    status VARCHAR(16) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_status_created (status, created_at),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
