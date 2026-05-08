CREATE TABLE IF NOT EXISTS risk_rule (
    rule_code VARCHAR(64) NOT NULL PRIMARY KEY,
    rule_value DECIMAL(20, 8) NOT NULL
);

INSERT IGNORE INTO risk_rule (rule_code, rule_value) VALUES ('MAX_LEVERAGE', 100);
