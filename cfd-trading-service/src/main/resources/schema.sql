CREATE TABLE IF NOT EXISTS t_open_position (
    order_id VARCHAR(64) NOT NULL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    open_price DECIMAL(20, 8) NOT NULL,
    quantity DECIMAL(20, 8) NOT NULL,
    leverage DECIMAL(20, 8) NOT NULL,
    margin DECIMAL(20, 8) NOT NULL,
    floating_pnl DECIMAL(20, 8) NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS cfd_outbox_message (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    topic VARCHAR(128) NOT NULL,
    message_key VARCHAR(128),
    payload TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    error_message VARCHAR(512),
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)
);
