DROP TABLE trades IF EXISTS;

CREATE TABLE trades  (
    transaction_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    ticker VARCHAR(20),
    security_description VARCHAR(100),
    price DECIMAL(128,10) NOT NULL,
    quantity INTEGER NOT NULL,
    reporting_currency VARCHAR(4),
    fx_rate DECIMAL(128,10),
    trade_type VARCHAR(5) NOT NULL,
    transaction_date DATE
);