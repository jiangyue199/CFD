CREATE TABLE IF NOT EXISTS clearing_account_balance (
    user_id VARCHAR(64) NOT NULL PRIMARY KEY,
    available DECIMAL(20, 8) NOT NULL DEFAULT 0,
    frozen_margin DECIMAL(20, 8) NOT NULL DEFAULT 0
);
