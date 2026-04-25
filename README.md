# CFD 平台（Spring Cloud Alibaba）

这是一个基于 **Spring Cloud Alibaba + Kafka** 的 CFD 微服务示例工程，包含：

- `cfd-common-kafka`：公共 Kafka 组件库
  - Topic 创建接口
  - 可靠生产（acks=all、生产端幂等、重试）
  - 消费端幂等执行器（基于 consumer-group + messageId 去重）
  - Outbox 事件表与 Relay（保障本地业务变更与消息发送一致性）
- `cfd-risk-service`：风控服务
- `cfd-order-service`：订单服务
- `cfd-trading-service`：交易服务
- `cfd-clearing-service`：资金清算服务

## 核心业务链路

1. APP 调用 `order-service` 开仓接口
2. `order-service` 调用 `risk-service` 风控校验
3. 风控通过后，订单服务落单 + Outbox，发送 `OrderOpenCommand` 给交易服务
4. `trading-service` 消费指令后写入开仓记录，计算保证金/浮盈亏
5. 交易服务发送两类消息：
   - `TradeOpenedEvent` 给清算服务（扣减可用资金、冻结保证金）
   - `TradeOpenedFeedback` 给订单服务（更新订单开仓成功）

## 一致性与幂等策略

- **发送可靠性**：Kafka Producer 开启 `acks=all`、`enable.idempotence=true`、重试与超时策略
- **生产侧一致性**：业务变更与消息入 Outbox 放在同一本地事务，Relay 异步投递
- **消费侧幂等**：`IdempotentConsumerExecutor` 按 `consumerGroup + messageId` 去重
- **业务层幂等**：订单号/开仓单号在仓储层唯一约束（示例中使用 `saveIfAbsent`）

## 运行测试

```bash
mvn test
```

> 单元/集成测试使用内存 Kafka Broker 模拟真实事件流，验证开仓链路与重复消息防重逻辑。
