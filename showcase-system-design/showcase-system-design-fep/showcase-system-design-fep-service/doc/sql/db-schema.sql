
CREATE TABLE payment_order (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  request_id VARCHAR(64) UNIQUE,
  user_id BIGINT,
  amount BIGINT,
  status VARCHAR(32)
);
