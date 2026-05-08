CREATE TABLE IF NOT EXISTS user_profile (
    user_id VARCHAR(64) NOT NULL PRIMARY KEY,
    username VARCHAR(128),
    email VARCHAR(256),
    phone VARCHAR(32),
    kyc_passed TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)
);
