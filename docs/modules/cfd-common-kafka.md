# cfd-common-kafka 模块文档

## 模块概述

`cfd-common-kafka` 是 CFD 平台的 Kafka 公共组件库，封装了分布式消息系统中常用的可靠性模式：

- **Transactional Outbox Pattern** — 保障本地事务与消息发送的最终一致性
- **Idempotent Consumer** — 消费侧去重，防止重复处理
- **Reliable Producer** — 生产侧可靠性配置（acks=all + 幂等生产）
- **KafkaEnvelope** — 统一消息信封，携带 messageId 用于全链路追踪和幂等

## 包结构

```
com.cfd.common.kafka
├── config/
│   └── KafkaReliabilityConfiguration.java   # Spring 自动配置
├── idempotent/
│   ├── ConsumerDedupStore.java              # 去重存储接口
│   ├── IdempotentConsumerExecutor.java      # 幂等执行器
│   ├── InMemoryAndExpiryConsumerDedupStore  # 带 TTL 的内存去重
│   └── InMemoryConsumerDedupStore.java      # 简单内存去重（测试用）
├── message/
│   └── KafkaEnvelope.java                   # 消息信封泛型类
├── outbox/
│   ├── OutboxMessage.java                   # Outbox 消息实体
│   ├── OutboxStatus.java                    # 状态枚举
│   ├── OutboxRepository.java               # 仓储接口
│   ├── RetryableOutboxRepository.java       # 扩展接口（支持清理）
│   ├── OutboxRelayService.java              # Relay 服务（轮询+发送）
│   ├── OutboxRelayScheduler.java            # 定时调度器
│   ├── InMemoryOutboxRepository.java        # 内存实现（单测）
│   ├── InMemoryAndRetryOutboxRepository     # 内存实现（集成测试）
│   └── persistence/
│       ├── OutboxMessageEntity.java         # MyBatis-Plus 实体
│       ├── OutboxMessageDbMapper.java       # MyBatis-Plus Mapper
│       └── MybatisPlusOutboxRepository.java # MySQL 实现
├── producer/
│   ├── ReliableKafkaPublisher.java          # 发布接口
│   ├── SpringKafkaReliablePublisher.java    # KafkaTemplate 实现
│   ├── InMemoryReliablePublisher.java       # 内存实现（测试）
│   └── KafkaPublishException.java           # 发布异常
├── test/
│   └── InMemoryKafkaBroker.java             # 内存 Kafka 模拟
└── topic/
    ├── TopicAdminService.java               # Topic 管理接口
    └── SpringKafkaTopicAdminService.java    # Spring 实现
```

## 核心机制

### 1. Transactional Outbox Pattern

```
┌─────────────────────────────────────────────────┐
│            Application Service                   │
│                                                  │
│  @Transactional                                  │
│  1. 执行业务逻辑 (INSERT/UPDATE)                 │
│  2. outboxRepository.save(message)              │
│     ↓ 同一事务提交                               │
└──────────────────────────┬──────────────────────┘
                           │
┌──────────────────────────▼──────────────────────┐
│         OutboxRelayScheduler (@Scheduled)         │
│                                                   │
│  每 500ms:                                        │
│  1. outboxRepository.listPending(batchSize)      │
│  2. reliablePublisher.publish(topic, key, payload)│
│  3. outboxRepository.markPublished(id)           │
│     或 markFailed(id, error)                     │
└──────────────────────────────────────────────────┘
```

### 2. Idempotent Consumer

```
┌────────────────────────────────────────────────┐
│         IdempotentConsumerExecutor               │
│                                                  │
│  execute(consumerGroup, messageId, payload, fn): │
│  1. dedupStore.tryStartProcessing(group, msgId) │
│     → false: 重复消息，跳过                      │
│     → true: 继续执行                             │
│  2. fn.accept(payload)                           │
│  3. dedupStore.markProcessed(group, msgId)       │
│     异常时: clearProcessing(group, msgId) + rethrow│
└────────────────────────────────────────────────┘
```

### 3. KafkaEnvelope 消息信封

```json
{
  "messageId": "uuid",
  "eventType": "OrderOpenCommand",
  "sourceService": "order-service",
  "occurredAt": "2024-01-01T00:00:00Z",
  "businessKey": "order-001",
  "payload": { ... }
}
```

## 配置项

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `spring.kafka.bootstrap-servers` | `localhost:9092` | Kafka 集群地址 |
| `cfd.kafka.outbox.fixed-delay-ms` | `500` | Outbox Relay 轮询间隔 (ms) |
| `cfd.outbox.relay.batch-size` | `200` | 每次轮询最大批量 |

## 使用方式

各业务服务通过 Maven 依赖引入本模块：

```xml
<dependency>
    <groupId>com.cfd</groupId>
    <artifactId>cfd-common-kafka</artifactId>
    <version>${project.version}</version>
</dependency>
```

在 `@SpringBootApplication` 的 `scanBasePackages` 中添加 `com.cfd.common.kafka` 即可自动注入所有 Bean。
