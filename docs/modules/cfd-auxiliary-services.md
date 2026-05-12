# 辅助服务文档

## cfd-market-data-service 行情服务

### 概述
行情服务管理交易品种的实时报价数据，提供行情查询和更新接口。

- **端口**: 8087
- **数据库**: `cfd_market_data`
- **核心表**: `market_quote`

### API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/quotes/{symbol}` | 查询指定品种行情 |
| POST | `/quotes` | 更新行情数据 |

### 数据模型

| 字段 | 类型 | 说明 |
|------|------|------|
| symbol | VARCHAR(32) | 交易品种（如 BTCUSDT） |
| bid | DECIMAL(20,8) | 买入价 |
| ask | DECIMAL(20,8) | 卖出价 |
| quote_time | TIMESTAMP | 报价时间 |

---

## cfd-notification-service 通知服务

### 概述
通知服务负责向用户发送多渠道通知消息（如短信、邮件、站内信）。

- **端口**: 8088
- **数据库**: `cfd_notification`
- **核心表**: `notification_message`

### API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/notifications/send` | 发送通知 |
| GET | `/notifications/user/{userId}` | 查询用户通知历史 |

### 数据模型

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 自增主键 |
| user_id | VARCHAR(64) | 目标用户 |
| channel | VARCHAR(32) | 通道（SMS/EMAIL/PUSH） |
| message | TEXT | 消息内容 |
| sent_at | TIMESTAMP | 发送时间 |

---

## cfd-reporting-service 报表服务

### 概述
报表服务负责生成和管理日报数据，支持按日期和报表类型查询。

- **端口**: 8089
- **数据库**: `cfd_reporting`
- **核心表**: `daily_report`

### API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/reports/generate` | 生成日报 |
| GET | `/reports/{businessDate}` | 按日期查询报表 |

---

## cfd-config-service 配置服务

### 概述
配置服务提供运行时配置的 CRUD 管理，支持动态修改系统参数。

- **端口**: 8090
- **数据库**: `cfd_config`
- **核心表**: `runtime_config`

### API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/configs/{key}` | 查询配置项 |
| POST | `/configs` | 创建/更新配置 |
| GET | `/configs` | 列出所有配置 |

### 数据模型

Key-Value 结构：`config_key` → `config_value`

---

## cfd-backend-management-service 后台管理服务

### 概述
后台管理服务为运营团队提供仪表盘指标查看和管理能力。

- **端口**: 8091
- **数据库**: `cfd_backend`
- **核心表**: `dashboard_metric`

### API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/dashboard/metrics` | 查询所有仪表盘指标 |
| GET | `/dashboard/metrics/{key}` | 查询指定指标 |
| POST | `/dashboard/metrics` | 更新指标 |
