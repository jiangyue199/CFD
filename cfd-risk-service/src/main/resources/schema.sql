CREATE TABLE IF NOT EXISTS risk_rule (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    rule_code VARCHAR(64) NOT NULL,
    rule_name VARCHAR(128) NOT NULL,
    symbol VARCHAR(32) NOT NULL DEFAULT '*',
    rule_value DECIMAL(20, 8) NOT NULL,
    priority INT NOT NULL DEFAULT 0,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    description VARCHAR(256) DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_rule_code_symbol (rule_code, symbol)
);

CREATE TABLE IF NOT EXISTS risk_blacklist (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    list_type VARCHAR(32) NOT NULL COMMENT 'BLACK|WHITE',
    target_type VARCHAR(32) NOT NULL COMMENT 'USER_ID|IP|DEVICE|CARD',
    target_value VARCHAR(256) NOT NULL COMMENT '被列入名单的值',
    reason VARCHAR(256) DEFAULT NULL COMMENT '列入原因',
    expires_at TIMESTAMP NULL COMMENT '有效期截止时间',
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_list_target (list_type, target_type, target_value)
);

CREATE TABLE IF NOT EXISTS user_status (
    user_id VARCHAR(64) NOT NULL PRIMARY KEY,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE|FROZEN|SUSPENDED',
    frozen_reason VARCHAR(256) DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS symbol_permission (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(32) NOT NULL,
    user_level VARCHAR(32) NOT NULL COMMENT 'RETAIL|PRO|INSTITUTIONAL',
    region VARCHAR(32) NOT NULL COMMENT 'CN|EU|US|GLOBAL',
    allowed TINYINT(1) NOT NULL DEFAULT 1,
    direction VARCHAR(16) NOT NULL DEFAULT 'BOTH' COMMENT 'BUY|SELL|BOTH',
    hedge_allowed TINYINT(1) NOT NULL DEFAULT 1,
    min_lot DECIMAL(20, 8) NOT NULL DEFAULT 0.001,
    max_lot DECIMAL(20, 8) NOT NULL DEFAULT 1000,
    step_lot DECIMAL(20, 8) NOT NULL DEFAULT 0.001,
    max_leverage DECIMAL(10, 2) NOT NULL DEFAULT 50,
    min_stop_level DECIMAL(20, 8) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_symbol_level_region (symbol, user_level, region)
);

CREATE TABLE IF NOT EXISTS daily_trade_limit (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    symbol VARCHAR(32) NOT NULL DEFAULT '*',
    daily_max_lots DECIMAL(20, 8) NOT NULL DEFAULT 1000,
    daily_max_orders INT NOT NULL DEFAULT 100,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_symbol (user_id, symbol)
);

CREATE TABLE IF NOT EXISTS position_limit (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(32) NOT NULL,
    per_symbol_limit DECIMAL(20, 8) NOT NULL DEFAULT 100,
    global_net_exposure DECIMAL(20, 8) NOT NULL DEFAULT 1000000,
    currency VARCHAR(16) NOT NULL DEFAULT 'USD',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_symbol (symbol)
);

CREATE TABLE IF NOT EXISTS trading_session (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(32) NOT NULL,
    day_of_week INT NOT NULL COMMENT '1-7 (Monday=1)',
    start_time TIME NOT NULL COMMENT '交易开始时间',
    end_time TIME NOT NULL COMMENT '交易结束时间',
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_symbol_day (symbol, day_of_week)
);

CREATE TABLE IF NOT EXISTS news_event (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(32) NOT NULL DEFAULT '*',
    event_time TIMESTAMP NOT NULL COMMENT '事件发生时间',
    impact_level VARCHAR(16) NOT NULL COMMENT 'LOW|MEDIUM|HIGH',
    pre_event_lock_seconds INT NOT NULL DEFAULT 60 COMMENT '事件前锁定秒数',
    post_event_lock_seconds INT NOT NULL DEFAULT 60 COMMENT '事件后锁定秒数',
    description VARCHAR(256) DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_daily_trade (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    trade_date DATE NOT NULL,
    total_lots DECIMAL(20, 8) NOT NULL DEFAULT 0,
    order_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_symbol_date (user_id, symbol, trade_date)
);

INSERT IGNORE INTO risk_rule (rule_code, rule_name, symbol, rule_value, priority, description) VALUES
('MAX_LEVERAGE', '全局最大杠杆限制', '*', 50, 100, '全局最大杠杆倍数'),
('MAX_QUANTITY', '全局最大数量限制', '*', 1000, 90, '全局最大交易数量'),
('MAX_EXPOSURE', '全局最大风险敞口', '*', 50000, 80, '全局最大风险敞口(数量×杠杆)'),
('MAX_LEVERAGE', 'BTC最大杠杆限制', 'BTCUSDT', 20, 110, 'BTC品种最大杠杆倍数'),
('MAX_QUANTITY', 'BTC最大数量限制', 'BTCUSDT', 100, 95, 'BTC品种最大交易数量');

INSERT IGNORE INTO symbol_permission (symbol, user_level, region, allowed, direction, hedge_allowed, min_lot, max_lot, step_lot, max_leverage, min_stop_level) VALUES
('EURUSD', 'RETAIL', 'CN', 1, 'BOTH', 1, 0.01, 100, 0.01, 50, 0.0001),
('EURUSD', 'PRO', 'CN', 1, 'BOTH', 1, 0.01, 500, 0.01, 100, 0.0001),
('BTCUSDT', 'RETAIL', 'CN', 1, 'BOTH', 1, 0.001, 10, 0.001, 20, 5),
('BTCUSDT', 'PRO', 'CN', 1, 'BOTH', 1, 0.001, 100, 0.001, 50, 5),
('GOLD', 'RETAIL', 'CN', 1, 'BOTH', 1, 0.01, 50, 0.01, 20, 0.1),
('EURUSD', 'RETAIL', 'EU', 1, 'BOTH', 1, 0.01, 100, 0.01, 30, 0.0001),
('BTCUSDT', 'RETAIL', 'EU', 0, 'BOTH', 0, 0.001, 0, 0.001, 0, 0),
('EURUSD', 'RETAIL', 'US', 1, 'BOTH', 1, 0.01, 100, 0.01, 50, 0.0001);

INSERT IGNORE INTO daily_trade_limit (user_id, symbol, daily_max_lots, daily_max_orders) VALUES
('demo-user', '*', 100, 50);

INSERT IGNORE INTO position_limit (symbol, per_symbol_limit, global_net_exposure, currency) VALUES
('EURUSD', 50, 1000000, 'USD'),
('BTCUSDT', 10, 1000000, 'USD'),
('GOLD', 30, 1000000, 'USD');

INSERT IGNORE INTO trading_session (symbol, day_of_week, start_time, end_time, enabled) VALUES
('EURUSD', 1, '00:00:00', '23:59:59', 1),
('EURUSD', 2, '00:00:00', '23:59:59', 1),
('EURUSD', 3, '00:00:00', '23:59:59', 1),
('EURUSD', 4, '00:00:00', '23:59:59', 1),
('EURUSD', 5, '00:00:00', '23:59:59', 1),
('EURUSD', 6, '00:00:00', '23:59:59', 1),
('EURUSD', 7, '00:00:00', '23:59:59', 1),
('BTCUSDT', 1, '00:00:00', '23:59:59', 1),
('BTCUSDT', 2, '00:00:00', '23:59:59', 1),
('BTCUSDT', 3, '00:00:00', '23:59:59', 1),
('BTCUSDT', 4, '00:00:00', '23:59:59', 1),
('BTCUSDT', 5, '00:00:00', '23:59:59', 1),
('BTCUSDT', 6, '00:00:00', '23:59:59', 1),
('BTCUSDT', 7, '00:00:00', '23:59:59', 1);

-- ========================================
-- 账户与保证金风控相关表
-- ========================================

-- 账户保证金快照
CREATE TABLE IF NOT EXISTS account_margin (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    balance DECIMAL(20, 8) NOT NULL DEFAULT 0 COMMENT '账户余额',
    equity DECIMAL(20, 8) NOT NULL DEFAULT 0 COMMENT '账户权益',
    used_margin DECIMAL(20, 8) NOT NULL DEFAULT 0 COMMENT '已用保证金',
    free_margin DECIMAL(20, 8) NOT NULL DEFAULT 0 COMMENT '可用保证金',
    margin_level DECIMAL(10, 4) NOT NULL DEFAULT 0 COMMENT '保证金水平(%)',
    floating_pnl DECIMAL(20, 8) NOT NULL DEFAULT 0 COMMENT '浮动盈亏',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id)
);

-- 保证金配置
CREATE TABLE IF NOT EXISTS margin_config (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_level VARCHAR(32) NOT NULL DEFAULT 'RETAIL' COMMENT '用户等级',
    margin_call_level DECIMAL(10, 4) NOT NULL DEFAULT 100 COMMENT '追加保证金预警线(%)',
    stop_out_level DECIMAL(10, 4) NOT NULL DEFAULT 50 COMMENT '强平线(%)',
    negative_balance_protection TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用负余额保护',
    concentration_ratio DECIMAL(10, 4) NOT NULL DEFAULT 80 COMMENT '单一品种集中度预警阈值(%)',
    floating_loss_ratio DECIMAL(10, 4) NOT NULL DEFAULT 30 COMMENT '浮动亏损过快预警阈值(%)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_level (user_level)
);

-- 持仓保证金信息
CREATE TABLE IF NOT EXISTS position_margin (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL COMMENT '订单ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    symbol VARCHAR(32) NOT NULL COMMENT '品种',
    direction VARCHAR(16) NOT NULL COMMENT 'BUY|SELL',
    quantity DECIMAL(20, 8) NOT NULL COMMENT '持仓数量',
    open_price DECIMAL(20, 8) NOT NULL COMMENT '开仓价',
    current_price DECIMAL(20, 8) NOT NULL COMMENT '当前价',
    leverage DECIMAL(10, 2) NOT NULL COMMENT '杠杆',
    used_margin DECIMAL(20, 8) NOT NULL COMMENT '占用保证金',
    floating_pnl DECIMAL(20, 8) NOT NULL COMMENT '浮动盈亏',
    swap DECIMAL(20, 8) NOT NULL DEFAULT 0 COMMENT '隔夜利息',
    status VARCHAR(16) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN|CLOSED|FORCE_CLOSED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_id (order_id),
    KEY idx_user_id (user_id),
    KEY idx_user_symbol (user_id, symbol)
);

-- 风险告警记录
CREATE TABLE IF NOT EXISTS risk_alert (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    alert_type VARCHAR(32) NOT NULL COMMENT 'MARGIN_CALL|STOP_OUT|NEGATIVE_BALANCE|CONCENTRATION|FLOATING_LOSS|SWAP_WARNING',
    alert_level VARCHAR(16) NOT NULL COMMENT 'INFO|WARNING|CRITICAL',
    message VARCHAR(512) NOT NULL COMMENT '告警消息',
    margin_level DECIMAL(10, 4) DEFAULT NULL COMMENT '触发时的保证金水平',
    equity DECIMAL(20, 8) DEFAULT NULL COMMENT '触发时的权益',
    handled TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已处理',
    handled_at TIMESTAMP NULL COMMENT '处理时间',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_alert_type (alert_type),
    KEY idx_created_at (created_at)
);

-- 强平记录
CREATE TABLE IF NOT EXISTS force_close_record (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    order_id VARCHAR(64) NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    close_reason VARCHAR(32) NOT NULL COMMENT 'STOP_OUT|NEGATIVE_BALANCE|RISK_CONTROL',
    close_price DECIMAL(20, 8) NOT NULL COMMENT '强平价格',
    close_quantity DECIMAL(20, 8) NOT NULL COMMENT '强平数量',
    realized_pnl DECIMAL(20, 8) NOT NULL COMMENT '已实现盈亏',
    margin_before DECIMAL(20, 8) NOT NULL COMMENT '强平前保证金',
    margin_after DECIMAL(20, 8) NOT NULL COMMENT '强平后保证金',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_created_at (created_at)
);

-- 用户浮动亏损统计
CREATE TABLE IF NOT EXISTS user_floating_loss (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    trade_date DATE NOT NULL,
    start_equity DECIMAL(20, 8) NOT NULL COMMENT '当日初始权益',
    max_floating_loss DECIMAL(20, 8) NOT NULL DEFAULT 0 COMMENT '当日最大浮动亏损',
    current_floating_loss DECIMAL(20, 8) NOT NULL DEFAULT 0 COMMENT '当前浮动亏损',
    loss_ratio DECIMAL(10, 4) NOT NULL DEFAULT 0 COMMENT '亏损比例(%)',
    alert_triggered TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已触发预警',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_date (user_id, trade_date)
);

-- 默认保证金配置
INSERT IGNORE INTO margin_config (user_level, margin_call_level, stop_out_level, negative_balance_protection, concentration_ratio, floating_loss_ratio) VALUES
('RETAIL', 100, 50, 1, 80, 30),
('PRO', 80, 30, 1, 90, 50),
('INSTITUTIONAL', 50, 20, 0, 95, 70);

