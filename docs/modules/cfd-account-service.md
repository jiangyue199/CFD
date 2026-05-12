# cfd-account-service 账户服务文档

## 服务概述

账户服务管理用户的多币种资金账户，提供充值、提现和余额查询能力。

- **端口**: 8086
- **数据库**: `cfd_account`
- **核心表**: `account_currency_balance`

## 包结构

```
com.cfd.account
├── api/
│   ├── AccountController.java             # REST 接口
│   ├── AccountBalanceResponse.java        # 余额响应 DTO
│   └── AccountOperationRequest.java       # 充值/提现请求 DTO
├── client/
│   └── AccountServiceFeignClient.java     # Feign 客户端
├── config/
│   └── AccountModuleConfiguration.java
├── domain/
│   ├── AccountCurrencyBalance.java        # 账户余额聚合
│   ├── AccountBalanceRepository.java      # 仓储接口
│   └── InMemoryAccountBalanceRepository.java
├── persistence/
│   ├── AccountCurrencyBalanceEntity.java  # MyBatis-Plus 实体
│   ├── AccountCurrencyBalanceDbMapper.java
│   └── MybatisPlusAccountBalanceRepository.java
└── service/
    └── AccountApplicationService.java     # 应用服务
```

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/accounts/{userId}/{currency}` | 查询指定币种余额 |
| POST | `/accounts/deposit` | 充值 |
| POST | `/accounts/withdraw` | 提现 |

## 数据模型

主键为 `userId + currency` 组合，支持多币种账户。
