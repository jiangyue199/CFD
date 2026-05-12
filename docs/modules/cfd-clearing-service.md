# cfd-clearing-service 清算服务文档

## 服务概述

清算服务负责 CFD 交易的资金结算。当交易服务完成开仓后，清算服务消费交易事件，执行保证金冻结操作：从用户可用余额中扣减保证金金额，并增加冻结保证金。

- **端口**: 8084
- **数据库**: `cfd_clearing`
- **核心表**: `clearing_account_balance`

## 业务流程

```
Kafka: cfd.trade.opened.event
    │
    ▼
TradeOpenedConsumer (幂等消费)
    │
    ▼
ClearingApplicationService.handleTradeOpened()
    │
    ├── 1. 查找账户 (不存在则创建默认余额 100,000)
    ├── 2. 检查可用余额 ≥ margin
    └── 3. 执行 debitMargin:
           available -= margin
           frozenMargin += margin
```

## 包结构

```
com.cfd.clearing
├── api/
│   └── ClearingQueryController.java   # REST: 查询余额
├── client/
│   └── ClearingServiceFeignClient.java
├── config/
│   └── ClearingModuleConfiguration.java
├── domain/
│   ├── AccountBalance.java            # 账户余额聚合
│   ├── AccountRepository.java         # 仓储接口
│   └── InMemoryAccountRepository.java
├── messaging/
│   └── TradeOpenedConsumer.java       # Kafka 消费
├── persistence/
│   ├── AccountBalanceEntity.java      # MyBatis-Plus 实体
│   ├── AccountBalanceDbMapper.java
│   └── MybatisPlusAccountRepository.java
└── service/
    └── ClearingApplicationService.java # 清算逻辑
```

## API 接口

### GET /accounts/{userId}/balance

查询用户清算账户余额。

**响应:**
```json
{
  "userId": "demo-user",
  "available": 95000.00000000,
  "frozenMargin": 5000.00000000
}
```

## Kafka 消息

### 消费

| Topic | Consumer Group | 处理逻辑 |
|-------|----------------|----------|
| `cfd.trade.opened.event` | `clearing-trade-opened` | 冻结保证金 |

## 领域模型: AccountBalance

```java
class AccountBalance {
    String userId;
    BigDecimal available;      // 可用余额
    BigDecimal frozenMargin;   // 已冻结保证金

    void debitMargin(BigDecimal margin) {
        available -= margin;
        frozenMargin += margin;
    }
}
```
