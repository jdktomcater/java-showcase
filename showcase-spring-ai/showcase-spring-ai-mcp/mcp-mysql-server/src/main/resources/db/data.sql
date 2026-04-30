INSERT INTO t_user (username, email, age, city) VALUES
    ('alice',   'alice@example.com',   28, '上海'),
    ('bob',     'bob@example.com',     32, '北京'),
    ('charlie', 'charlie@example.com', 25, '深圳'),
    ('david',   'david@example.com',   40, '杭州'),
    ('eva',     'eva@example.com',     22, '成都');

INSERT INTO t_order (user_id, product, amount, status) VALUES
    (1, 'MacBook Pro 14',   18999.00, 'PAID'),
    (1, 'AirPods Pro 2',     1899.00, 'PAID'),
    (2, 'iPhone 15 Pro',     7999.00, 'PAID'),
    (3, 'iPad Air',          4799.00, 'REFUND'),
    (4, 'Apple Watch S9',    2999.00, 'PAID'),
    (5, 'HomePod mini',       749.00, 'PENDING');
