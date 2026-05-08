CREATE TABLE IF NOT EXISTS order_record (
    order_id VARCHAR(64) NOT NULL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    status_reason VARCHAR(512),
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
