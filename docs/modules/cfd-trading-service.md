# cfd-trading-service 交易服务文档

## 服务概述

交易服务负责 CFD 持仓的创建和管理。它从 Kafka 消费开仓指令，执行开仓逻辑（计算保证金），并将交易结果事件发布给清算服务和订单服务。

- **端口**: 8083
- **数据库**: `cfd_trading`
- **核心表**: `t_open_position`, `cfd_outbox_message`

## 业务流程

```
Kafka: cfd.order.open.command
    │
    ▼
OrderOpenConsumer (幂等消费)
    │
    ▼
TradingApplicationService.handleOrderOpen()
    │
    ├── 1. 幂等检查: findByOrderId → 已存在则跳过
    ├── 2. 计算保证金: margin = openPrice × quantity ÷ leverage
    ├── 3. 创建 OpenPosition (status=OPENED)
    ├── 4. 写入 Outbox: TradeOpenedEvent (→ 清算服务)
    └── 5. 写入 Outbox: TradeOpenedFeedback (→ 订单服务)
```

## 保证金计算公式

```
margin = openPrice × quantity ÷ leverage

示例: 1 BTC @ $50,000, 10x 杠杆
margin = 50000 × 1 ÷ 10 = $5,000
```

## 包结构

```
com.cfd.trading
├── api/
│   └── TradingQueryController.java    # REST 查询接口
├── client/
│   └── TradingServiceFeignClient.java # Feign 客户端
├── config/
│   ├── TradingModuleConfiguration.java        # Bean 配置
│   └── TradingRelaySchedulerConfiguration.java # Outbox 调度
├── domain/
│   ├── OpenPosition.java              # 持仓聚合根
│   ├── OpenPositionRepository.java    # 仓储接口
│   ├── OpenPositionMapper.java        # DTO 映射
│   └── InMemoryOpenPositionRepository # 内存实现
├── messaging/
│   └── OrderOpenConsumer.java         # Kafka 消费: 开仓指令
├── persistence/
│   ├── OpenPositionEntity.java        # MyBatis-Plus 实体
│   ├── OpenPositionDbMapper.java      # Mapper
│   └── MybatisPlusOpenPositionRepository.java
└── service/
    ├── TradingApplicationService.java   # 核心交易逻辑
    └── OpenPositionQueryService.java    # 查询服务
```

## API 接口

### GET /positions/{orderId}

按订单号查询持仓详情。

**响应:**
```json
{
  "orderId": "order-001",
  "userId": "user-001",
  "symbol": "BTCUSDT",
  "openPrice": 50000.00,
  "quantity": 1.00,
  "leverage": 10.00,
  "margin": 5000.00,
  "floatingPnl": 0.00,
  "status": "OPENED",
  "createdAt": "2024-01-01T00:00:00Z"
}
```

### GET /positions/user/{userId}

查询用户所有持仓。

## Kafka 消息

### 消费

| Topic | Consumer Group | 处理逻辑 |
|-------|----------------|----------|
| `cfd.order.open.command` | `trading-order-open` | 创建持仓 + 发布事件 |

### 生产 (Outbox)

| Topic | 消息类型 | 说明 |
|-------|----------|------|
| `cfd.trade.opened.event` | `TradeOpenedEvent` | 通知清算服务处理资金 |
| `cfd.trade.opened.feedback` | `TradeOpenedFeedback` | 反馈订单服务更新状态 |
