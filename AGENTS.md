# AGENTS.md

## Cursor Cloud specific instructions

### Project Overview

CFD (Contract for Difference) Trading Platform — a Spring Cloud Alibaba microservice system with event-driven messaging via Kafka (Transactional Outbox Pattern).

### Prerequisites

- **Java 17** (set as default via `update-alternatives`)
- **Maven 3.8+** (system-installed)
- **Docker** (for MySQL 8.0 and Apache Kafka 3.7)
- `JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64`

### Infrastructure Services

Start Docker containers before running application services:

```bash
sudo dockerd &>/tmp/dockerd.log &
sleep 3
docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 mysql:8.0
docker run -d --name kafka \
  -p 9092:9092 \
  -e KAFKA_NODE_ID=1 -e KAFKA_PROCESS_ROLES=broker,controller \
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

Wait ~15s for MySQL to be ready, then create the per-service databases. **`schema.sql` only creates tables, NOT the databases** — each service connects to a named DB (e.g. `cfd_risk`) that must already exist, or startup fails with `Unknown database 'cfd_xxx'`:

```bash
DBS="cfd_account cfd_backend cfd_clearing cfd_config cfd_market_data cfd_notification cfd_order cfd_risk cfd_trading cfd_user cfd_reporting"
SQL=""; for db in $DBS; do SQL="$SQL CREATE DATABASE IF NOT EXISTS \`$db\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"; done
docker exec mysql mysql -uroot -proot -e "$SQL"
```

### Building & Testing

```bash
mvn clean install -DskipTests    # compile + install to local repo
mvn test                          # runs in-memory integration tests (no infra needed)
```

### Running Core Services

Services must be started from their module directory:

```bash
cd cfd-risk-service && mvn spring-boot:run      # port 8081
cd cfd-order-service && mvn spring-boot:run     # port 8082
cd cfd-trading-service && mvn spring-boot:run   # port 8083
cd cfd-clearing-service && mvn spring-boot:run  # port 8084
```

All services depend on `cfd-domain` and `cfd-common-kafka` being installed to the local Maven repository first (`mvn install` from root).

### Key Gotchas

1. **`mvn install` required before `spring-boot:run`** — shared modules (`cfd-domain`, `cfd-common-kafka`) must be in the local Maven repo.
2. **Schema files** (`src/main/resources/schema.sql`) auto-run on startup with `spring.sql.init.mode: always` and create *tables* with `IF NOT EXISTS`. They do **not** create the databases themselves — create the per-service databases first (see Infrastructure Services).
3. **Nacos discovery is disabled** in all services (`nacos.discovery.enabled: false`). Feign clients use direct URLs.
4. **Outbox relay** runs on a `@Scheduled` timer (default 500ms–1000ms). After submitting an order, allow ~2–5 seconds for the full async chain to complete.
5. **Docker in this environment** requires the `fuse-overlayfs` storage driver and `iptables-legacy`. With Docker 29+, the `containerd-snapshotter` feature must also be disabled for fuse-overlayfs to work. See `/etc/docker/daemon.json` (`{"storage-driver":"fuse-overlayfs","features":{"containerd-snapshotter":false}}`).
6. **MySQL/Kafka containers are not auto-started** on VM boot (only the toolchain is baked into the snapshot). Start `dockerd`, then the `mysql` and `kafka` containers, then (re)create the databases each fresh session before running services.
7. **The application code lives on feature branches, not `main`** (which is a stub). The Maven build only works on a branch that contains `pom.xml` and the `cfd-*` modules.

### API Quick Reference

| Service | Port | Key Endpoints |
|---------|------|---------------|
| Risk | 8081 | `POST /risk/open/check` |
| Order | 8082 | `POST /orders/open`, `GET /orders/{id}` |
| Trading | 8083 | `GET /positions/{orderId}`, `GET /positions/user/{userId}` |
| Clearing | 8084 | `GET /accounts/{userId}/balance` |

### Workflow Test Command

```bash
curl -X POST http://localhost:8082/orders/open \
  -H "Content-Type: application/json" \
  -d '{"orderId":"test-001","userId":"demo-user","symbol":"BTCUSDT","openPrice":50000,"quantity":1,"leverage":10}'
```

Then verify with: `curl http://localhost:8082/orders/test-001` (expect `"status":"OPENED"` after ~3s).
