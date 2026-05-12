# CFD 差价合约交易平台

> 基于 **Spring Cloud Alibaba + Apache Kafka** 的微服务架构 CFD 交易系统

## 项目概述

本项目是一个完整的差价合约 (CFD, Contract for Difference) 交易平台，采用微服务架构设计，涵盖了 CFD 交易的完整生命周期：用户管理、账户管理、行情数据、订单提交、风险控制、交易执行、资金清算、通知推送、报表生成和后台管理。

核心交易链路采用 **事件驱动架构**，通过 Kafka 实现服务间异步通信，并使用 **Transactional Outbox Pattern** 保障本地事务与消息发送的一致性。

## 技术栈

| 分类 | 技术 |
|------|------|
| 语言 | Java 17 |
| 框架 | Spring Boot 3.2.5 |
| 微服务 | Spring Cloud 2023.0.1 |
| 服务发现 | Spring Cloud Alibaba (Nacos) 2023.0.1.0 |
| 服务间调用 | Spring Cloud OpenFeign |
| 消息队列 | Apache Kafka |
| ORM | MyBatis-Plus 3.5.16 |
| 数据库 | MySQL 8.0 |
| 构建工具 | Apache Maven |

## 系统架构

```
┌──────────────────────────────────────────────────────────────────────────┐
│                           客户端 (APP / Web)                              │
└──────────────────────────────────┬───────────────────────────────────────┘
                                   │ HTTP
                                   ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                        cfd-order-service (8082)                           │
│                          订单服务 - 业务入口                               │
│  接收开仓请求 → 调用风控 → 落单 → 写入 Outbox → Relay 投递 Kafka           │
└──────┬───────────────────────────┬───────────────────────────────────────┘
       │ Feign (同步)              │ Kafka (异步)
       ▼                           ▼
┌─────────────────┐    ┌─────────────────────────────────────────────────┐
│ cfd-risk-service│    │             Apache Kafka                         │
│    (8081)       │    │  Topics:                                         │
│  风控服务       │    │    • cfd.order.open.command                       │
│  杠杆/数量校验  │    │    • cfd.trade.opened.event                       │
└─────────────────┘    │    • cfd.trade.opened.feedback                    │
                       └─────┬───────────────────────────┬────────────────┘
                             │                           │
                             ▼                           ▼
                ┌─────────────────────┐    ┌─────────────────────────┐
                │ cfd-trading-service  │    │ cfd-clearing-service     │
                │       (8083)        │    │       (8084)             │
                │     交易服务         │    │     清算服务              │
                │ 消费开仓指令         │    │ 消费交易事件              │
                │ → 创建持仓           │    │ → 扣减可用资金            │
                │ → 计算保证金         │    │ → 冻结保证金              │
                │ → 发布交易事件       │    └─────────────────────────┘
                │ → 发布反馈给订单服务  │
                └─────────────────────┘
```

## 核心业务链路

### 开仓流程 (Open Position Workflow)

```
1. 用户提交开仓请求 → Order Service (POST /orders/open)
2. Order Service → Risk Service (Feign 同步调用风控校验)
3. 风控通过 → 订单落库 + Outbox 写入 OrderOpenCommand (同一事务)
4. Outbox Relay 定时轮询 → 投递 OrderOpenCommand 到 Kafka
5. Trading Service 消费 → 创建持仓记录 + 计算保证金
6. Trading Service → 发布 TradeOpenedEvent (→ Clearing Service)
7. Trading Service → 发布 TradeOpenedFeedback (→ Order Service)
8. Clearing Service 消费 → 扣减可用资金、冻结保证金
9. Order Service 消费反馈 → 更新订单状态为 OPENED
```

### 一致性与幂等策略

| 策略 | 实现方式 |
|------|----------|
| **生产可靠性** | Kafka Producer: `acks=all` + `enable.idempotence=true` + 重试 |
| **生产侧一致性** | Transactional Outbox Pattern：业务变更与消息入 Outbox 在同一本地事务 |
| **消费侧幂等** | `IdempotentConsumerExecutor`：按 `consumerGroup + messageId` 去重 |
| **业务层幂等** | 仓储层唯一约束（`saveIfAbsent` 语义） |

## 模块说明

### 核心模块

| 模块 | 端口 | 说明 |
|------|------|------|
| `cfd-domain` | - | 共享领域模型（DTO、事件、命令、枚举） |
| `cfd-common-kafka` | - | Kafka 公共组件库（Outbox、幂等消费、可靠生产） |

### 核心业务服务

| 模块 | 端口 | 说明 |
|------|------|------|
| `cfd-order-service` | 8082 | 订单服务 - 开仓入口，编排风控→落单→异步指令 |
| `cfd-risk-service` | 8081 | 风控服务 - 杠杆/数量阈值校验 |
| `cfd-trading-service` | 8083 | 交易服务 - 开仓执行，保证金计算 |
| `cfd-clearing-service` | 8084 | 清算服务 - 资金变动，保证金冻结 |

### 辅助业务服务

| 模块 | 端口 | 说明 |
|------|------|------|
| `cfd-account-service` | 8086 | 账户服务 - 多币种余额管理 |
| `cfd-user-service` | 8085 | 用户服务 - 注册、KYC、资料管理 |
| `cfd-market-data-service` | 8087 | 行情服务 - 实时报价管理 |
| `cfd-notification-service` | 8088 | 通知服务 - 多渠道消息推送 |
| `cfd-reporting-service` | 8089 | 报表服务 - 日报生成 |
| `cfd-config-service` | 8090 | 配置服务 - 运行时配置管理 |
| `cfd-backend-management-service` | 8091 | 后台管理服务 - 仪表盘指标 |

## 快速开始

### 前提条件

- Java 17+
- Maven 3.8+
- Docker（用于运行 MySQL 和 Kafka）

### 启动基础设施

```bash
# MySQL
docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 mysql:8.0

# Kafka (KRaft 模式，无需 ZooKeeper)
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  -e CLUSTER_ID=MkU3OEVBNTcwNTJENDM2Qk \
  apache/kafka:3.7.0
```

### 构建项目

```bash
mvn clean install -DskipTests
```

### 启动核心服务

```bash
# 风控服务
cd cfd-risk-service && mvn spring-boot:run &

# 订单服务
cd cfd-order-service && mvn spring-boot:run &

# 交易服务
cd cfd-trading-service && mvn spring-boot:run &

# 清算服务
cd cfd-clearing-service && mvn spring-boot:run &
```

### 测试开仓流程

```bash
# 提交开仓订单
curl -X POST http://localhost:8082/orders/open \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order-001",
    "userId": "demo-user",
    "symbol": "BTCUSDT",
    "openPrice": 50000,
    "quantity": 1,
    "leverage": 10
  }'

# 等待异步链路完成 (约 3 秒)
sleep 3

# 查询订单状态 (预期: OPENED)
curl http://localhost:8082/orders/order-001

# 查询持仓 (预期: margin=5000)
curl http://localhost:8083/positions/order-001

# 查询账户余额 (预期: available=95000, frozenMargin=5000)
curl http://localhost:8084/accounts/demo-user/balance
```

## 运行测试

```bash
# 运行所有测试（使用内存 Kafka Broker，无需外部依赖）
mvn test
```

集成测试 `CfdWorkflowIntegrationTest` 完整验证开仓链路：
- 订单提交 → 风控通过 → 交易执行 → 资金清算 → 状态反馈
- 重复消息防重（幂等性验证）
- 风控拒绝场景

## API 参考

### 订单服务 (8082)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/orders/open` | 提交开仓订单 |
| GET | `/orders/{orderId}` | 查询订单状态 |

### 风控服务 (8081)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/risk/open/check` | 开仓风控校验 |

### 交易服务 (8083)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/positions/{orderId}` | 按订单号查持仓 |
| GET | `/positions/user/{userId}` | 按用户查持仓列表 |

### 清算服务 (8084)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/accounts/{userId}/balance` | 查询账户余额 |

### 账户服务 (8086)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/accounts/{userId}/{currency}` | 查询币种余额 |
| POST | `/accounts/deposit` | 充值 |
| POST | `/accounts/withdraw` | 提现 |

### 用户服务 (8085)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/users/register` | 用户注册 |
| GET | `/users/{userId}` | 查询用户信息 |

### 行情服务 (8087)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/quotes/{symbol}` | 查询行情 |
| POST | `/quotes` | 更新行情 |

## 项目结构

```
cfd-platform/
├── pom.xml                              # 父 POM (依赖版本管理)
├── cfd-domain/                          # 共享领域模型
│   └── src/main/java/com/cfd/domain/
│       ├── kafka/Topics.java            # Kafka Topic 常量
│       └── model/                       # DTO、命令、事件、枚举
├── cfd-common-kafka/                    # Kafka 公共组件
│   └── src/main/java/com/cfd/common/kafka/
│       ├── config/                      # 自动配置
│       ├── idempotent/                  # 幂等消费器
│       ├── message/                     # 消息信封
│       ├── outbox/                      # Outbox 模式实现
│       ├── producer/                    # 可靠生产者
│       ├── test/                        # 测试工具
│       └── topic/                       # Topic 管理
├── cfd-risk-service/                    # 风控服务
├── cfd-order-service/                   # 订单服务
├── cfd-trading-service/                 # 交易服务
├── cfd-clearing-service/                # 清算服务
├── cfd-account-service/                 # 账户服务
├── cfd-user-service/                    # 用户服务
├── cfd-market-data-service/             # 行情服务
├── cfd-notification-service/            # 通知服务
├── cfd-reporting-service/               # 报表服务
├── cfd-config-service/                  # 配置服务
└── cfd-backend-management-service/      # 后台管理服务
```

## 设计原则

1. **DDD 分层架构**：每个服务内部分为 `api`（接口层）、`service`（应用层）、`domain`（领域层）、`persistence`（基础设施层）
2. **Outbox Pattern**：保障分布式事务最终一致性
3. **幂等消费**：消费侧通过 `consumerGroup + messageId` 去重，防止重复处理
4. **领域隔离**：每个服务独立数据库，通过事件驱动实现跨服务数据同步
5. **测试友好**：核心逻辑提供 InMemory 实现，集成测试无需外部依赖
