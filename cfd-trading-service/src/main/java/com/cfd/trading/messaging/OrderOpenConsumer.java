package com.cfd.trading.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cfd.common.kafka.idempotent.IdempotentConsumerExecutor;
import com.cfd.common.kafka.message.KafkaEnvelope;
import com.cfd.domain.model.OrderOpenCommand;
import com.cfd.trading.service.TradingApplicationService;

@Component
public class OrderOpenConsumer {

    private static final String GROUP_ID = "trading-order-open";

    private final ObjectMapper objectMapper;
    private final IdempotentConsumerExecutor idempotentConsumerExecutor;
    private final TradingApplicationService tradingApplicationService;

    public OrderOpenConsumer(ObjectMapper objectMapper,
                             IdempotentConsumerExecutor idempotentConsumerExecutor,
                             TradingApplicationService tradingApplicationService) {
        this.objectMapper = objectMapper;
        this.idempotentConsumerExecutor = idempotentConsumerExecutor;
        this.tradingApplicationService = tradingApplicationService;
    }

    @KafkaListener(topics = "${cfd.topic.orderOpen:cfd.order.open.command}", groupId = GROUP_ID)
    public void consume(String payload) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(KafkaEnvelope.class, OrderOpenCommand.class);
        KafkaEnvelope<OrderOpenCommand> envelope = objectMapper.readValue(payload, type);

        idempotentConsumerExecutor.execute(
                GROUP_ID,
                envelope.messageId(),
                envelope.payload(),
                tradingApplicationService::handleOrderOpen);
    }
}
