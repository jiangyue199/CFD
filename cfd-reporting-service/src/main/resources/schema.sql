CREATE TABLE IF NOT EXISTS daily_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_date VARCHAR(16) NOT NULL,
    report_type VARCHAR(64),
    generated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    message TEXT
);
