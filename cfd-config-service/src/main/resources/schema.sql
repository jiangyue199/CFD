CREATE TABLE IF NOT EXISTS runtime_config (
    config_key VARCHAR(128) NOT NULL PRIMARY KEY,
    config_value VARCHAR(1024)
);
