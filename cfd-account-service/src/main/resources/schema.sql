CREATE TABLE IF NOT EXISTS account_currency_balance (
    pk VARCHAR(128) NOT NULL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    currency VARCHAR(16) NOT NULL,
    available DECIMAL(20, 8) NOT NULL DEFAULT 0
);
