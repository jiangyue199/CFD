# cfd-risk-service 风控服务文档

## 服务概述

风控服务提供开仓前的风险校验能力。订单服务在接受用户开仓请求后，同步调用风控服务进行杠杆和数量的阈值校验。

- **端口**: 8081
- **数据库**: `cfd_risk`
- **核心表**: `risk_rule`

## 风控规则

| 规则代码 | 默认值 | 说明 |
|----------|--------|------|
| `MAX_LEVERAGE` | 50 | 最大允许杠杆倍数 |
| `MAX_QUANTITY` | 1000 | 最大允许交易数量 |

校验逻辑：
- `leverage > MAX_LEVERAGE` → 拒绝 ("Leverage exceeds threshold")
- `quantity > MAX_QUANTITY` → 拒绝 ("Quantity exceeds threshold")
- 其他情况 → 通过 ("PASS")

## 包结构

```
com.cfd.risk
├── api/
│   └── RiskController.java        # REST: POST /risk/open/check
├── client/
│   └── RiskServiceFeignClient.java
└── persistence/
    ├── RiskRuleEntity.java        # 风控规则实体
    └── RiskRuleDbMapper.java      # MyBatis-Plus Mapper
```

## API 接口

### POST /risk/open/check

执行开仓风控校验。

**请求体:**
```json
{
  "userId": "user-001",
  "symbol": "BTCUSDT",
  "quantity": 1.00,
  "leverage": 10.00
}
```

**响应 (通过):**
```json
{ "allowed": true, "reason": "PASS" }
```

**响应 (拒绝):**
```json
{ "allowed": false, "reason": "Leverage exceeds threshold" }
```

## 设计说明

- 规则存储在数据库中，支持运行时动态调整
- 服务启动时自动初始化默认规则（`ensureDefaults()`）
- 被订单服务通过 Feign 同步调用
