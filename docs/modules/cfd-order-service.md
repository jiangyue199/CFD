# cfd-order-service 订单服务文档

## 服务概述

订单服务是 CFD 交易平台的业务入口，负责接收用户的开仓请求，编排风控校验，并通过 Outbox + Kafka 异步触发下游交易流程。

- **端口**: 8082
- **数据库**: `cfd_order`
- **核心表**: `order_record`, `cfd_outbox_message`

## 业务流程

```
HTTP POST /orders/open
    │
    ▼
OrderController
    │
    ▼
OrderApplicationService.submitOpenOrder()
    │
    ├── 1. 幂等检查: findById(orderId) → 已存在则直接返回
    ├── 2. 创建待风控订单: status = PENDING_RISK
    ├── 3. 调用风控: RiskFeignClient.checkOpenRisk()
    │       ├── 拒绝 → 标记 RISK_REJECTED → 返回
    │       └── 通过 → 继续
    ├── 4. 封装 OrderOpenCommand + KafkaEnvelope
    ├── 5. 写入 Outbox (同一事务)
    ├── 6. 更新订单状态: SENT_TO_TRADING
    └── 7. 返回 OrderResponse
```

## 包结构

```
com.cfd.order
├── api/
│   ├── OrderController.java          # REST 接口
│   └── OpenOrderHttpRequest.java     # HTTP 请求 DTO
├── client/
│   ├── RiskFeignClient.java          # 风控服务 Feign 客户端
│   └── OrderServiceFeignClient.java  # 供其他服务调用本服务
├── config/
│   ├── OrderModuleConfiguration.java         # Bean 配置
│   └── OrderRelaySchedulerConfiguration.java # Outbox Relay 调度
├── domain/
│   ├── OrderAggregate.java           # 订单聚合根
│   ├── OrderDomainService.java       # 领域服务（状态转换）
│   ├── OrderRepository.java          # 仓储接口
│   ├── OrderMapper.java              # 实体→DTO 映射
│   └── InMemoryOrderRepository.java  # 内存实现（测试用）
├── messaging/
│   └── OrderTradeFeedbackConsumer.java # Kafka 消费: 交易反馈
├── persistence/
│   ├── OrderEntity.java              # MyBatis-Plus 实体
│   ├── OrderDbMapper.java            # MyBatis-Plus Mapper
│   └── MybatisPlusOrderRepository.java # MySQL 仓储实现
└── service/
    └── OrderApplicationService.java  # 应用服务（核心编排逻辑）
```

## API 接口

### POST /orders/open

提交开仓订单。

**请求体:**
```json
{
  "orderId": "order-001",
  "userId": "user-001",
  "symbol": "BTCUSDT",
  "openPrice": 50000.00,
  "quantity": 1.00,
  "leverage": 10.00
}
```

**响应:**
```json
{
  "orderId": "order-001",
  "userId": "user-001",
  "symbol": "BTCUSDT",
  "status": "SENT_TO_TRADING",
  "createdAt": "2024-01-01T00:00:00Z"
}
```

### GET /orders/{orderId}

查询订单状态。

## Kafka 消息

### 生产 (Outbox)

| Topic | 消息类型 | 触发条件 |
|-------|----------|----------|
| `cfd.order.open.command` | `OrderOpenCommand` | 风控通过后 |

### 消费

| Topic | Consumer Group | 处理逻辑 |
|-------|----------------|----------|
| `cfd.trade.opened.feedback` | `order-service-feedback` | 更新订单状态为 OPENED |

## 配置项

```yaml
spring.datasource.url: jdbc:mysql://localhost:3306/cfd_order
services.risk.url: http://localhost:8081    # 风控服务地址
cfd.topic.orderOpen: cfd.order.open.command
cfd.topic.tradeFeedback: cfd.trade.opened.feedback
cfd.outbox.relay.fixed-delay-ms: 1000
```
