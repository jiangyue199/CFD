package com.cfd.trading.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cfd.common.kafka.message.KafkaEnvelope;
import com.cfd.common.kafka.outbox.OutboxMessage;
import com.cfd.common.kafka.outbox.OutboxRepository;
import com.cfd.domain.kafka.Topics;
import com.cfd.domain.model.OrderOpenCommand;
import com.cfd.domain.model.TradeOpenedEvent;
import com.cfd.domain.model.TradeOpenedFeedback;
import com.cfd.trading.domain.OpenPosition;
import com.cfd.trading.domain.OpenPositionRepository;

@Service
public class TradingApplicationService {

    private final OpenPositionRepository openPositionRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public TradingApplicationService(OpenPositionRepository openPositionRepository,
                                     OutboxRepository outboxRepository,
                                     ObjectMapper objectMapper) {
        this.openPositionRepository = openPositionRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public synchronized void handleOrderOpen(OrderOpenCommand command) {
        if (openPositionRepository.findByOrderId(command.orderId()).isPresent()) {
            return;
        }

        BigDecimal margin = command.openPrice()
                .multiply(command.quantity())
                .divide(command.leverage(), 8, RoundingMode.HALF_UP);
        BigDecimal floatingPnl = BigDecimal.ZERO;

        OpenPosition position = new OpenPosition(
                command.orderId(),
                command.userId(),
                command.symbol(),
                command.openPrice(),
                command.quantity(),
                command.leverage(),
                margin,
                floatingPnl);

        openPositionRepository.saveIfAbsent(position);

        TradeOpenedEvent tradeOpened = new TradeOpenedEvent(
                command.orderId(),
                command.userId(),
                command.symbol(),
                command.openPrice(),
                command.quantity(),
                command.leverage(),
                margin,
                floatingPnl);

        TradeOpenedFeedback feedback = new TradeOpenedFeedback(command.orderId(), "OPEN_SUCCESS");

        outboxRepository.save(new OutboxMessage(
                UUID.randomUUID().toString(),
                Topics.TRADE_OPENED_EVENT,
                command.orderId(),
                toJson(new KafkaEnvelope<>(
                        UUID.randomUUID().toString(),
                        "TradeOpenedEvent",
                        "trading-service",
                        Instant.now(),
                        command.orderId(),
                        tradeOpened)),
                Instant.now()));

        outboxRepository.save(new OutboxMessage(
                UUID.randomUUID().toString(),
                Topics.TRADE_OPENED_FEEDBACK,
                command.orderId(),
                toJson(new KafkaEnvelope<>(
                        UUID.randomUUID().toString(),
                        "TradeOpenedFeedback",
                        "trading-service",
                        Instant.now(),
                        command.orderId(),
                        feedback)),
                Instant.now()));
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize trading event", ex);
        }
    }
}
