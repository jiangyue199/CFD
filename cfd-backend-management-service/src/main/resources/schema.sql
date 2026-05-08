CREATE TABLE IF NOT EXISTS dashboard_metric (
    metric_key VARCHAR(128) NOT NULL PRIMARY KEY,
    metric_value VARCHAR(512)
);
