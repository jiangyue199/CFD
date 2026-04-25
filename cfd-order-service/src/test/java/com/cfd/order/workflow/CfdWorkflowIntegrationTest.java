package com.cfd.order.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cfd.clearing.domain.AccountRepository;
import com.cfd.clearing.domain.InMemoryAccountRepository;
import com.cfd.clearing.service.ClearingApplicationService;
import com.cfd.common.kafka.idempotent.IdempotentConsumerExecutor;
import com.cfd.common.kafka.idempotent.InMemoryConsumerDedupStore;
import com.cfd.common.kafka.message.KafkaEnvelope;
import com.cfd.common.kafka.outbox.InMemoryOutboxRepository;
import com.cfd.common.kafka.outbox.OutboxRelayService;
import com.cfd.common.kafka.producer.InMemoryReliablePublisher;
import com.cfd.common.kafka.test.InMemoryKafkaBroker;
import com.cfd.domain.kafka.Topics;
import com.cfd.domain.model.OrderOpenCommand;
import com.cfd.domain.model.OrderOpenRequest;
import com.cfd.domain.model.OrderStatus;
import com.cfd.domain.model.RiskCheckResponse;
import com.cfd.domain.model.TradeOpenedEvent;
import com.cfd.domain.model.TradeOpenedFeedback;
import com.cfd.order.client.RiskFeignClient;
import com.cfd.order.domain.InMemoryOrderRepository;
import com.cfd.order.domain.OrderDomainService;
import com.cfd.order.service.OrderApplicationService;
import com.cfd.trading.domain.InMemoryOpenPositionRepository;
import com.cfd.trading.service.TradingApplicationService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

class CfdWorkflowIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldCompleteOpenPositionWorkflowWithIdempotencyAndConsistency() throws Exception {
        InMemoryKafkaBroker broker = new InMemoryKafkaBroker();
        broker.createTopic(Topics.ORDER_OPEN_COMMAND);
        broker.createTopic(Topics.TRADE_OPENED_EVENT);
        broker.createTopic(Topics.TRADE_OPENED_FEEDBACK);

        IdempotentConsumerExecutor idempotentExecutor = new IdempotentConsumerExecutor(new InMemoryConsumerDedupStore());

        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryOutboxRepository orderOutbox = new InMemoryOutboxRepository();
        OutboxRelayService orderRelay = new OutboxRelayService(orderOutbox, new InMemoryReliablePublisher(broker));

        RiskFeignClient riskFeignClient = request -> new RiskCheckResponse(true, "PASS");
        OrderApplicationService orderService = new OrderApplicationService(
                riskFeignClient,
                orderRepository,
                new OrderDomainService(),
                orderOutbox,
                objectMapper);

        InMemoryOpenPositionRepository positionRepository = new InMemoryOpenPositionRepository();
        InMemoryOutboxRepository tradingOutbox = new InMemoryOutboxRepository();
        OutboxRelayService tradingRelay = new OutboxRelayService(tradingOutbox, new InMemoryReliablePublisher(broker));
        TradingApplicationService tradingService = new TradingApplicationService(positionRepository, tradingOutbox, objectMapper);

        AccountRepository accountRepository = new InMemoryAccountRepository();
        accountRepository.saveIfAbsent(new com.cfd.clearing.domain.AccountBalance("demo-user", new BigDecimal("100000.00000000"), BigDecimal.ZERO));
        ClearingApplicationService clearingService = new ClearingApplicationService(accountRepository);

        JavaType orderCommandType = objectMapper.getTypeFactory().constructParametricType(KafkaEnvelope.class, OrderOpenCommand.class);
        JavaType tradeEventType = objectMapper.getTypeFactory().constructParametricType(KafkaEnvelope.class, TradeOpenedEvent.class);
        JavaType feedbackType = objectMapper.getTypeFactory().constructParametricType(KafkaEnvelope.class, TradeOpenedFeedback.class);

        broker.subscribe(Topics.ORDER_OPEN_COMMAND, record -> {
            try {
                KafkaEnvelope<OrderOpenCommand> envelope = objectMapper.readValue(record.payload(), orderCommandType);
                idempotentExecutor.execute("trading", envelope.messageId(), envelope.payload(), tradingService::handleOrderOpen);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        broker.subscribe(Topics.TRADE_OPENED_EVENT, record -> {
            try {
                KafkaEnvelope<TradeOpenedEvent> envelope = objectMapper.readValue(record.payload(), tradeEventType);
                idempotentExecutor.execute("clearing", envelope.messageId(), envelope.payload(), clearingService::handleTradeOpened);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        broker.subscribe(Topics.TRADE_OPENED_FEEDBACK, record -> {
            try {
                KafkaEnvelope<TradeOpenedFeedback> envelope = objectMapper.readValue(record.payload(), feedbackType);
                idempotentExecutor.execute("order-feedback", envelope.messageId(), envelope.payload(), orderService::onTradeOpened);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        orderService.submitOpenOrder(new OrderOpenRequest(
                "order-001",
                "demo-user",
                "BTCUSDT",
                new BigDecimal("50000"),
                new BigDecimal("1"),
                new BigDecimal("10")
        ));

        orderRelay.flushPending(10);
        tradingRelay.flushPending(10);

        assertTrue(positionRepository.findByOrderId("order-001").isPresent());
        assertEquals(OrderStatus.OPENED, orderRepository.findById("order-001").orElseThrow().getStatus());

        BigDecimal expectedMargin = new BigDecimal("5000.00000000");
        BigDecimal expectedAvailable = new BigDecimal("95000.00000000");
        assertEquals(expectedMargin, positionRepository.findByOrderId("order-001").orElseThrow().getMargin());
        assertEquals(expectedAvailable, accountRepository.findByUserId("demo-user").orElseThrow().getAvailable());

        String alreadyProcessedTradeEvent = broker.history(Topics.TRADE_OPENED_EVENT).get(0).payload();
        broker.send(Topics.TRADE_OPENED_EVENT, "order-001", alreadyProcessedTradeEvent);
        broker.send(Topics.TRADE_OPENED_EVENT, "order-001", alreadyProcessedTradeEvent);

        assertEquals(expectedAvailable, accountRepository.findByUserId("demo-user").orElseThrow().getAvailable());
    }

    @Test
    void shouldRejectOrderWhenRiskCheckFails() {
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryOutboxRepository orderOutbox = new InMemoryOutboxRepository();

        RiskFeignClient riskFeignClient = request -> new RiskCheckResponse(false, "Leverage exceeds threshold");
        OrderApplicationService orderService = new OrderApplicationService(
                riskFeignClient,
                orderRepository,
                new OrderDomainService(),
                orderOutbox,
                objectMapper);

        String orderId = UUID.randomUUID().toString();
        orderService.submitOpenOrder(new OrderOpenRequest(
                orderId,
                "user-2",
                "ETHUSDT",
                new BigDecimal("3000"),
                new BigDecimal("2"),
                new BigDecimal("100")
        ));

        assertEquals(OrderStatus.RISK_REJECTED, orderRepository.findById(orderId).orElseThrow().getStatus());
    }
}
