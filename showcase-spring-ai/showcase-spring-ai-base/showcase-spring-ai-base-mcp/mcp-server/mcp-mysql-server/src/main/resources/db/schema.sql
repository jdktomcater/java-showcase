DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(64)  NOT NULL,
    email       VARCHAR(128) NOT NULL,
    age         INT          NOT NULL,
    city        VARCHAR(64)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS t_order;
CREATE TABLE t_order (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT         NOT NULL,
    product     VARCHAR(128)   NOT NULL,
    amount      DECIMAL(12, 2) NOT NULL,
    status      VARCHAR(32)    NOT NULL,
    created_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);
