package com.cfd.clearing.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cfd.clearing.service.ClearingApplicationService;
import com.cfd.common.kafka.idempotent.IdempotentConsumerExecutor;
import com.cfd.common.kafka.message.KafkaEnvelope;
import com.cfd.domain.model.TradeOpenedEvent;

@Component
public class TradeOpenedConsumer {

    private static final String GROUP_ID = "clearing-trade-opened";

    private final ObjectMapper objectMapper;
    private final IdempotentConsumerExecutor idempotentConsumerExecutor;
    private final ClearingApplicationService clearingApplicationService;

    public TradeOpenedConsumer(ObjectMapper objectMapper,
                               IdempotentConsumerExecutor idempotentConsumerExecutor,
                               ClearingApplicationService clearingApplicationService) {
        this.objectMapper = objectMapper;
        this.idempotentConsumerExecutor = idempotentConsumerExecutor;
        this.clearingApplicationService = clearingApplicationService;
    }

    @KafkaListener(topics = "${cfd.topic.tradeOpened:cfd.trade.opened.event}", groupId = GROUP_ID)
    public void consume(String payload) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(KafkaEnvelope.class, TradeOpenedEvent.class);
        KafkaEnvelope<TradeOpenedEvent> envelope = objectMapper.readValue(payload, type);

        idempotentConsumerExecutor.execute(
                GROUP_ID,
                envelope.messageId(),
                envelope.payload(),
                clearingApplicationService::handleTradeOpened);
    }
}
