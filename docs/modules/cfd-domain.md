# cfd-domain 模块文档

## 模块概述

`cfd-domain` 是 CFD 平台的共享领域模型模块，定义了所有微服务之间通信所使用的数据传输对象（DTO）、领域事件、命令和枚举类型。该模块不包含任何业务逻辑，仅作为服务间契约的定义。

## 包结构

```
com.cfd.domain
├── kafka
│   └── Topics.java          # Kafka Topic 名称常量
└── model
    ├── OrderOpenRequest.java       # 开仓请求 (HTTP 入参)
    ├── OrderOpenCommand.java       # 开仓指令 (Kafka 命令)
    ├── OrderResponse.java          # 订单查询响应
    ├── OrderStatus.java            # 订单状态枚举
    ├── RiskCheckRequest.java       # 风控校验请求
    ├── RiskCheckResponse.java      # 风控校验响应
    ├── TradeOpenedEvent.java       # 交易开仓事件 (Kafka 事件)
    ├── TradeOpenedFeedback.java    # 交易反馈 (Kafka 反馈)
    ├── OpenPositionResponse.java   # 持仓查询响应
    ├── PositionStatus.java         # 持仓状态枚举
    └── AccountBalanceResponse.java # 账户余额响应
```

## 核心类说明

### Kafka Topics

| 常量 | Topic 名称 | 说明 |
|------|-----------|------|
| `ORDER_OPEN_COMMAND` | `cfd.order.open.command` | 订单服务 → 交易服务 |
| `TRADE_OPENED_EVENT` | `cfd.trade.opened.event` | 交易服务 → 清算服务 |
| `TRADE_OPENED_FEEDBACK` | `cfd.trade.opened.feedback` | 交易服务 → 订单服务 |

### 订单状态流转

```
PENDING_RISK → RISK_REJECTED (风控拒绝)
PENDING_RISK → SENT_TO_TRADING → OPENED (正常流程)
```

### 消息流向

```
OrderOpenRequest (HTTP)
    ↓
OrderOpenCommand (Kafka: order → trading)
    ↓
TradeOpenedEvent (Kafka: trading → clearing)
TradeOpenedFeedback (Kafka: trading → order)
```

## 设计说明

- 所有模型使用 Java 17 `record` 类型，确保不可变性
- 模块无外部依赖（除 `java.math.BigDecimal` 和 `java.time.Instant`）
- 被所有微服务通过 Maven 依赖引入
