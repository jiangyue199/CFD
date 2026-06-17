# 数字货币出入金服务（cfd-crypto-funding-service）技术方案

> 状态：**待评审（DRAFT / REVIEW）** — 评审通过后再进入开发
> 作者：CFD Platform Team
> 适用版本：cfd-platform 1.0.0-SNAPSHOT

## 0. 修订记录

| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| v0.1 | 2026-06-17 | CFD Platform Team | 初稿，供评审 |

---

## 1. 背景与目标

当前 CFD 平台已具备完整的交易主链路（下单 → 风控 → 撮合开仓 → 清算），资金侧由 `cfd-account-service`（币种可用余额台账）与 `cfd-clearing-service`（保证金可用/冻结）承载，但**资金来源与去向仅停留在内部记账**，缺少与外部区块链的真实出入金通道。

本方案新增一个独立微服务 **`cfd-crypto-funding-service`（数字货币出入金服务）**，作为平台与公链之间的"资金网关 + 链上资产台账"，负责：

- **入金（Deposit）**：为用户分配充值地址、监听链上到账、达到确认数后入账。
- **出金（Withdrawal）**：受理提现申请、风控/审核、冻结、签名广播、确认后扣账，失败可退回。
- **链上资产台账与对账（Ledger & Reconciliation）**：维护用户在各链/各币种的可用与冻结余额，并与链上、托管钱包定期对账。

### 1.1 设计目标

1. **架构一致**：完全复用现有约定（DDD 分层、`cfd-common-kafka` 事务性发件箱、幂等消费、MyBatis-Plus、Feign 直连 URL、单服务单库 + `schema.sql`、`InMemory*` 测试替身）。
2. **资金安全与一致性**：出入金记账满足幂等与"恰好一次"语义；任何链上动作与本地账本变更通过发件箱保证最终一致。
3. **可插拔链适配**：通过 `ChainGateway` SPI 抽象具体公链，本地/测试用 `MockChainGateway`，生产可接入节点 RPC 或第三方托管。
4. **可独立交付**：新服务自包含，对存量服务的改动降到最小，并以"评审决策点"形式显式列出需要其他服务配合的改动。

---

## 2. 范围（Scope）

### 2.1 In Scope（本期）

- 充值地址分配与查询（按 用户 × 链 × 币种）。
- 入金检测与入账（确认数门限、孤块/回滚处理框架）。
- 出金申请、风控/审核（自动 + 大额人工）、冻结、广播、确认、扣账/退回。
- 链上资产台账（available / frozen）与流水（double-entry 风格）。
- 出入金事件发布（供账户、通知、报表服务消费）。
- `ChainGateway` SPI 与 `MockChainGateway`（用于本地与集成测试，能跑通端到端）。

### 2.2 Out of Scope（后续迭代）

- 真实公链节点/HSM/KMS 的生产级接入实现（本期仅定义接口与 Mock）。
- KYC 主流程（仅预留 AML/风控调用点）。
- 法币出入金、跨链桥、链上 swap。
- 冷钱包归集与人工运维台（仅预留事件与状态）。

---

## 3. 术语

| 术语 | 含义 |
|------|------|
| 入金单（DepositOrder） | 一笔链上到账对应的入账记录，幂等键为 `chain + txHash + vout` |
| 出金单（WithdrawalOrder） | 一笔用户提现申请的全生命周期记录，业务键为 `withdrawalId` |
| 确认数（confirmations） | 链上交易被打包后其上叠加的区块数，达到门限才视为终态 |
| 充值地址（DepositAddress） | 平台分配给用户用于接收某链资产的地址 |
| 链上资产台账（CryptoLedger） | 用户在 `用户 × 链 × 币种` 维度的 available / frozen 余额及流水 |
| ChainGateway | 屏蔽具体公链差异的网关 SPI（生成地址、查询/广播交易、查询确认数） |

---

## 4. 总体架构

### 4.1 在平台中的位置

```
                       ┌──────────────────────────────────────────────┐
   外部公链            │                CFD 平台                        │
 (BTC/ETH/TRON...)     │                                                │
        │  扫描/回调     │   ┌───────────────────────────────┐          │
        ▼              │   │   cfd-crypto-funding-service     │          │
 ┌─────────────┐  RPC  │   │   (数字货币出入金服务, :8092)     │          │
 │ ChainGateway│◄──────┼──►│  - 充值地址 / 入金检测 / 入账     │          │
 │  (节点/托管) │ 广播   │   │  - 出金申请 / 审核 / 广播 / 扣账   │         │
 └─────────────┘       │   │  - 链上资产台账 + 发件箱(Outbox)  │          │
                       │   └──────┬───────────────┬──────────┘          │
                       │   Feign  │ 同步风控        │ Kafka(事件)         │
                       │          ▼               ▼                      │
                       │   cfd-risk-service   cfd-account-service        │
                       │     (:8081)            (:8086)                  │
                       │                      cfd-notification/reporting │
                       └──────────────────────────────────────────────┘
```

- **同步调用（Feign 直连 URL）**：出金提交时调用 `cfd-risk-service` 做 AML/风控前置校验（与 `order → risk` 同一模式）。
- **异步事件（Kafka + Outbox）**：入金入账完成、出金完成/失败等，通过事务性发件箱发布，下游（账户、通知、报表）幂等消费。

### 4.2 服务基本信息

| 项 | 取值 |
|----|------|
| 模块名 | `cfd-crypto-funding-service` |
| 端口 | **8092**（现状 8081–8091 已占用，顺延） |
| 数据库 | `cfd_crypto_funding`（需在基础设施初始化时创建，见 `AGENTS.md`） |
| 应用类 | `com.cfd.funding.FundingServiceApplication` |
| 依赖 | `cfd-domain`、`cfd-common-kafka`、web、validation、mybatis-plus、mysql、openfeign、nacos-discovery(禁用) |

---

## 5. 领域模型与状态机

### 5.1 聚合 / 实体

- **DepositAddress**：`userId, chain, currency, address, derivePath, createdAt`（唯一约束 `user×chain×currency` 与 `address`）。
- **DepositOrder**：`depositId, userId, chain, currency, txHash, vout, amount, confirmations, status, address, creditedAt`。
- **WithdrawalOrder**：`withdrawalId, userId, chain, currency, toAddress, amount, fee, status, riskResult, reviewBy, txHash, confirmations, failReason, createdAt`。
- **CryptoLedgerBalance**：`pk(user:chain:currency), userId, chain, currency, available, frozen`。
- **CryptoLedgerEntry**（流水/分录）：`entryId, refType(DEPOSIT/WITHDRAWAL/FREEZE/UNFREEZE), refId, userId, chain, currency, direction(CREDIT/DEBIT), amount, balanceAfter, createdAt`。

### 5.2 入金状态机

```
DETECTED ──(达到确认数)──► CONFIRMED ──(记账成功)──► CREDITED
   │                                                  
   └──(孤块/回滚)──► ORPHANED                          
```

### 5.3 出金状态机

```
CREATED ─► RISK_CHECKING ─► APPROVED ─► BROADCASTING ─► CONFIRMING ─► COMPLETED
              │                │                              │
              ▼                ▼                              ▼
           REJECTED      (大额) PENDING_REVIEW            FAILED ─► REFUNDED
```

- `REJECTED`：风控不通过，解冻并结束。
- `PENDING_REVIEW`：超过自动放行阈值，进入人工审核（`APPROVED` / `REJECTED`）。
- `FAILED → REFUNDED`：广播或上链失败，解冻退回可用余额。

> 状态机统一由领域服务 `FundingDomainService` 校验流转合法性（参考 `OrderDomainService` 的 `isFinalState/mark*` 风格）。

---

## 6. 核心流程

### 6.1 入金流程

1. 用户请求 `GET /funding/deposit-address?userId&chain&currency`，若不存在则经 `ChainGateway.deriveAddress(...)` 分配并落库。
2. **扫描器**（`DepositScannerScheduler`，`@Scheduled`，参考 `OutboxRelayScheduler` 风格）周期性调用 `ChainGateway.listIncoming(chain, since)` 拉取到账（生产环境可由回调/webhook 触发）。
3. 对每笔到账以 `chain+txHash+vout` 幂等 upsert 为 `DepositOrder(DETECTED)`，记录当前确认数。
4. 确认数达到该链门限 → `CONFIRMED`；在同一本地事务内：
   - 台账 `available += amount`，写入 `CryptoLedgerEntry(CREDIT)`；
   - 写入 **Outbox** 一条 `DepositCredited` 事件；
   - `DepositOrder` 置 `CREDITED`。
5. 发件箱中继（`OutboxRelayScheduler`）将 `DepositCredited` 投递到 Kafka，下游幂等消费（账户加余额 / 通知 / 报表）。

### 6.2 出金流程

1. 用户 `POST /funding/withdrawals`（`withdrawalId` 由客户端或服务端生成，作为幂等键）。
2. 创建 `WithdrawalOrder(CREATED)`；**同一事务内冻结**：台账 `available -= amount(+fee)`、`frozen += amount(+fee)`，写 `CryptoLedgerEntry(FREEZE)`。
3. `RISK_CHECKING`：Feign 调 `cfd-risk-service` 做 AML/限额/地址白名单校验。
   - 不通过 → `REJECTED`，解冻。
   - 金额 > 自动放行阈值 → `PENDING_REVIEW`（等待 `POST /funding/withdrawals/{id}/review`）。
   - 通过 → `APPROVED`。
4. `BROADCASTING`：`ChainGateway.broadcast(...)` 签名广播，记录 `txHash` → `CONFIRMING`。
5. 扫描确认数达门限 → 同一事务：`frozen -= amount(+fee)`，写 `CryptoLedgerEntry(DEBIT)`，Outbox 写 `WithdrawalCompleted`，置 `COMPLETED`。
6. 广播/上链失败 → `FAILED`：解冻（`frozen→available`），Outbox 写 `WithdrawalFailed`，置 `REFUNDED`。

### 6.3 对账流程（框架）

- `ReconciliationScheduler` 周期性比对：本地台账 vs 链上余额 vs `DepositOrder/WithdrawalOrder` 汇总，差异写入告警/对账差异表（本期产出差异事件，自动修复留后续）。

---

## 7. 区块链接入抽象（ChainGateway SPI）

为隔离公链差异并保证可测试性，定义 SPI（命名与现有 `ReliableKafkaPublisher` 接口 + `InMemory*` 实现风格一致）：

```java
public interface ChainGateway {
    String chain();                                   // 适用链标识，如 "ETH"/"TRON"/"BTC"
    DepositAddress deriveAddress(String userId, String currency);
    List<ChainTx> listIncoming(String currency, long sinceBlock); // 扫描到账（或由 webhook 注入）
    int confirmations(String txHash);
    String broadcast(SignedWithdrawal req);            // 返回 txHash
}
```

- 本期提供 `MockChainGateway`（内存模拟到账与确认数推进），保证 **本地 + 集成测试可端到端跑通**，不依赖真实节点。
- 生产实现（`EvmChainGateway` / `TronChainGateway` 等）后续迭代接入，私钥签名走 KMS/HSM（见安全章节）。

---

## 8. 与账户/资金台账的集成（评审决策点 ★）

充值后的链上资产如何反映到"可交易余额"，存在两种集成方式，请评审确定：

| 方案 | 做法 | 优点 | 缺点 |
|------|------|------|------|
| **A. 事件驱动（推荐）** | funding 通过 Outbox 发 `DepositCredited`/`WithdrawalCompleted`；`cfd-account-service` 新增幂等消费者更新 `account_currency_balance` | 与平台异步/最终一致范式一致、解耦、可靠重试 | 需给 account-service 新增 Kafka 消费者与幂等去重 |
| **B. 同步 Feign** | funding 直接调 account-service `/accounts/deposit`、`/accounts/withdraw` | 改动小、实现快 | 同步耦合；account 现有接口非幂等，需补幂等键 |

**推荐 A**：funding-service 作为链上资产的权威台账（available/frozen），账户/可交易余额由下游消费事件聚合。出金冻结发生在 funding 本地台账（同步、强一致），结算与下游通知走异步事件。

> 无论选 A/B，account-service 侧的"加/减余额"都必须做幂等（按 `depositId`/`withdrawalId` 去重）。

---

## 9. 消息与事件设计

### 9.1 新增 Topic（追加到 `cfd-domain` 的 `Topics.java`）

```java
public static final String FUNDING_DEPOSIT_CREDITED   = "cfd.funding.deposit.credited";
public static final String FUNDING_WITHDRAWAL_COMPLETED = "cfd.funding.withdrawal.completed";
public static final String FUNDING_WITHDRAWAL_FAILED    = "cfd.funding.withdrawal.failed";
```

### 9.2 新增事件 DTO（`cfd-domain/model`，record 风格）

- `DepositCreditedEvent(depositId, userId, chain, currency, amount, txHash, creditedAt)`
- `WithdrawalCompletedEvent(withdrawalId, userId, chain, currency, amount, fee, txHash, completedAt)`
- `WithdrawalFailedEvent(withdrawalId, userId, chain, currency, amount, reason, failedAt)`

### 9.3 复用现有可靠投递机制

- 生产侧：业务事务内写 `OutboxMessage`（`KafkaEnvelope` 包裹，`messageId=UUID`、`businessKey=depositId/withdrawalId`），由本服务的 `OutboxRelayScheduler` 中继。
- 消费侧：下游用 `IdempotentConsumerExecutor` + `@KafkaListener`（参考 `TradeOpenedConsumer`）。

---

## 10. 数据库设计（`schema.sql`，建表用 `IF NOT EXISTS`）

```sql
CREATE TABLE IF NOT EXISTS deposit_address (
    pk          VARCHAR(160) NOT NULL PRIMARY KEY,  -- userId:chain:currency
    user_id     VARCHAR(64)  NOT NULL,
    chain       VARCHAR(32)  NOT NULL,
    currency    VARCHAR(16)  NOT NULL,
    address     VARCHAR(128) NOT NULL,
    derive_path VARCHAR(128),
    created_at  TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE KEY uk_address (chain, address)
);

CREATE TABLE IF NOT EXISTS deposit_order (
    deposit_id    VARCHAR(96)  NOT NULL PRIMARY KEY,  -- chain:txHash:vout
    user_id       VARCHAR(64)  NOT NULL,
    chain         VARCHAR(32)  NOT NULL,
    currency      VARCHAR(16)  NOT NULL,
    tx_hash       VARCHAR(128) NOT NULL,
    vout          INT          NOT NULL DEFAULT 0,
    amount        DECIMAL(36,18) NOT NULL,
    confirmations INT          NOT NULL DEFAULT 0,
    status        VARCHAR(32)  NOT NULL,
    address       VARCHAR(128),
    credited_at   TIMESTAMP(6) NULL,
    created_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS withdrawal_order (
    withdrawal_id VARCHAR(64)  NOT NULL PRIMARY KEY,
    user_id       VARCHAR(64)  NOT NULL,
    chain         VARCHAR(32)  NOT NULL,
    currency      VARCHAR(16)  NOT NULL,
    to_address    VARCHAR(128) NOT NULL,
    amount        DECIMAL(36,18) NOT NULL,
    fee           DECIMAL(36,18) NOT NULL DEFAULT 0,
    status        VARCHAR(32)  NOT NULL,
    risk_result   VARCHAR(255),
    review_by     VARCHAR(64),
    tx_hash       VARCHAR(128),
    confirmations INT          NOT NULL DEFAULT 0,
    fail_reason   VARCHAR(512),
    created_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS crypto_ledger_balance (
    pk        VARCHAR(160) NOT NULL PRIMARY KEY,      -- userId:chain:currency
    user_id   VARCHAR(64)  NOT NULL,
    chain     VARCHAR(32)  NOT NULL,
    currency  VARCHAR(16)  NOT NULL,
    available DECIMAL(36,18) NOT NULL DEFAULT 0,
    frozen    DECIMAL(36,18) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS crypto_ledger_entry (
    entry_id     VARCHAR(64) NOT NULL PRIMARY KEY,
    ref_type     VARCHAR(32) NOT NULL,               -- DEPOSIT/WITHDRAWAL/FREEZE/UNFREEZE
    ref_id       VARCHAR(96) NOT NULL,
    user_id      VARCHAR(64) NOT NULL,
    chain        VARCHAR(32) NOT NULL,
    currency     VARCHAR(16) NOT NULL,
    direction    VARCHAR(8)  NOT NULL,               -- CREDIT/DEBIT
    amount       DECIMAL(36,18) NOT NULL,
    balance_after DECIMAL(36,18) NOT NULL,
    created_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)
);

-- 复用 cfd-common-kafka 的发件箱表（与其它服务一致）
CREATE TABLE IF NOT EXISTS cfd_outbox_message (
    id           VARCHAR(64) NOT NULL PRIMARY KEY,
    topic        VARCHAR(128) NOT NULL,
    message_key  VARCHAR(128),
    payload      TEXT NOT NULL,
    status       VARCHAR(32) NOT NULL,
    error_message VARCHAR(512),
    created_at   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)
);
```

> 金额采用 `DECIMAL(36,18)` 以适配数字货币高精度（区别于平台现有的 `DECIMAL(20,8)`，请评审确认精度策略）。

---

## 11. API 设计（REST，前缀 `/funding`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | `/funding/deposit-address` | 查询/分配充值地址（`userId,chain,currency`） |
| GET  | `/funding/deposits/{depositId}` | 查询入金单 |
| GET  | `/funding/balances/{userId}` | 查询用户链上资产台账（available/frozen） |
| POST | `/funding/withdrawals` | 提交提现申请（幂等：`withdrawalId`） |
| GET  | `/funding/withdrawals/{id}` | 查询出金单 |
| POST | `/funding/withdrawals/{id}/review` | 大额人工审核（approve/reject） |
| POST | `/funding/_mock/chain/deposit` | （仅 dev/test，Mock 链注入到账，驱动端到端） |

请求/响应 DTO 用 `record` + `spring-boot-starter-validation`（参考 `AccountOperationRequest`）。

---

## 12. 幂等、一致性与异常处理

- **入金幂等**：`depositId = chain:txHash:vout` 主键去重，重复到账不会重复入账。
- **出金幂等**：`withdrawalId` 主键去重，重复提交返回既有单（参考 `OrderApplicationService` 的 `findById` 提前返回）。
- **本地强一致**：余额变更 + 流水 + Outbox 写入在同一 `@Transactional` 内完成（DB 事务）。
- **跨服务最终一致**：通过 Outbox + 下游 `IdempotentConsumerExecutor` 保证。
- **资金安全不变量**：`available >= 0`、`frozen >= 0`；出金先冻结后广播，失败必解冻。
- **链回滚/孤块**：入金达确认数前不入账；已入账后发生深度回滚 → 产出对账差异事件人工介入（本期不自动冲正）。

---

## 13. 安全设计

- **私钥与签名**：私钥不落库；签名经 KMS/HSM；`ChainGateway` 仅持有"广播"能力，签名隔离在独立签名组件（生产实现）。
- **热/冷钱包分层**：热钱包限额出金，超额需归集/冷钱包审批（预留事件与状态，不在本期实现）。
- **提现风控**：地址白名单、单笔/日累计限额、首次提现冷静期、AML 名单（经 risk-service）。
- **大额人工复核**：`PENDING_REVIEW` + 双人审核（审计字段 `review_by`）。
- **审计**：所有资金动作进 `crypto_ledger_entry`，事件全程可追踪（`KafkaEnvelope` 自带 `messageId/sourceService/occurredAt`）。

---

## 14. 配置项（`application.yml`，沿用现有键风格）

```yaml
spring:
  application: { name: cfd-crypto-funding-service }
  datasource:
    url: ${MYSQL_URL:jdbc:mysql://localhost:3306/cfd_crypto_funding?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC}
  sql: { init: { mode: always, schema-locations: classpath:schema.sql } }
  cloud: { nacos: { discovery: { enabled: false } } }
server: { port: 8092 }
services:
  risk: { url: http://localhost:8081 }
cfd:
  kafka: { outbox: { fixed-delay-ms: 500 } }
  funding:
    deposit: { scan-fixed-delay-ms: 3000 }
    confirmations: { ETH: 12, TRON: 19, BTC: 3 }
    withdrawal: { auto-approve-threshold: 1000 }
    chain-gateway: mock   # mock | evm | tron ...
```

---

## 15. 可观测性

- 关键指标：入金/出金各状态量、确认等待时长、Outbox 积压、对账差异数。
- 告警：出金 `FAILED` 率、长时间 `CONFIRMING`、台账不变量被破坏、对账差异。
- 日志：以 `withdrawalId/depositId` 作为追踪键贯穿全链路。

---

## 16. 测试方案

- **单元测试**：状态机流转、冻结/解冻不变量、金额精度。
- **集成测试**（参考 `CfdWorkflowIntegrationTest` + `InMemoryKafkaBroker`）：用 `MockChainGateway` + `InMemory*` 仓储跑通：
  - 入金：注入到账 → 推进确认数 → 断言 `CREDITED` 且台账增加、`DepositCredited` 事件投递。
  - 出金：申请 → 冻结 → 风控通过 → 广播 → 确认 → `COMPLETED` 且台账扣减。
  - 出金失败 → `REFUNDED` 且解冻。
- **端到端（dev）**：起 MySQL/Kafka + 本服务 + risk-service，用 `/funding/_mock/chain/deposit` 驱动，curl 验证（与现有 hello-world 风格一致）。

---

## 17. 模块结构（与现有约定一致）

```
cfd-crypto-funding-service/
├── pom.xml                       # parent=cfd-platform, 依赖 cfd-domain + cfd-common-kafka
└── src/main/
    ├── java/com/cfd/funding/
    │   ├── FundingServiceApplication.java
    │   ├── api/                  # FundingController, *Request/*Response (record)
    │   ├── service/              # DepositApplicationService, WithdrawalApplicationService
    │   ├── domain/               # 聚合 + Repository 接口 + FundingDomainService + InMemory*
    │   ├── persistence/          # *Entity + *DbMapper(BaseMapper) + MybatisPlus*Repository
    │   ├── chain/                # ChainGateway SPI + MockChainGateway
    │   ├── messaging/            # (可选)入金回调/事件消费
    │   ├── scheduler/            # DepositScannerScheduler, ReconciliationScheduler
    │   └── config/               # FundingModuleConfiguration, RelaySchedulerConfiguration
    └── resources/
        ├── application.yml
        └── schema.sql
```

需在根 `pom.xml` 的 `<modules>` 追加 `cfd-crypto-funding-service`。

---

## 18. 分阶段交付计划（按子系统 / 复杂度，非工期）

1. **阶段一｜骨架与台账**：建模块、库表、台账与流水、REST 查询、`MockChainGateway`、单元测试。
2. **阶段二｜入金链路**：扫描器 + 确认数门限 + 入账 + `DepositCredited` 事件 + 集成测试。
3. **阶段三｜出金链路**：申请/冻结/风控(Feign)/审核/广播/确认/扣账/失败退回 + 事件 + 集成测试。
4. **阶段四｜下游集成（依赖第 8 节决策）**：account-service 幂等消费事件入账；通知/报表订阅。
5. **阶段五｜对账与运维**：对账调度、差异告警、热/冷钱包与 KMS 接口位（生产实现单列）。

---

## 19. 评审决策点（请重点确认）★

1. **第 8 节**：充值入账与平台余额的集成方式 —— 推荐 **A 事件驱动**，是否同意？是否接受给 `cfd-account-service` 增加幂等 Kafka 消费者？
2. **服务边界**：funding-service 作为"链上资产权威台账"，与 `cfd-account-service`/`cfd-clearing-service` 的余额职责划分是否认可？是否需要统一资金账户视图（聚合服务）？
3. **金额精度**：是否采用 `DECIMAL(36,18)`（区别于现有 `DECIMAL(20,8)`）？
4. **端口/库命名**：`8092` / `cfd_crypto_funding` 是否符合规划？
5. **链范围**：本期 Mock + 接口；首批真实接入哪些链（ETH/ERC20-USDT、TRON/TRC20-USDT、BTC）？
6. **安全基线**：KMS/HSM、热冷钱包分层、提现白名单/限额/冷静期的最低要求？
7. **回滚冲正**：已入账后链回滚是否需要自动冲正，还是本期仅人工对账？

---

> 评审通过后，将按第 18 节阶段一启动开发，并补充 `AGENTS.md` 中的新库（`cfd_crypto_funding`）初始化说明与服务启动方式。
