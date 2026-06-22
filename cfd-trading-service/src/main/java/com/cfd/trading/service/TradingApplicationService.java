package com.cfd.trading.service;

import com.cfd.common.kafka.message.KafkaEnvelope;
import com.cfd.common.kafka.outbox.OutboxMessage;
import com.cfd.common.kafka.outbox.RetryableOutboxRepository;
import com.cfd.domain.kafka.Topics;
import com.cfd.domain.model.OrderOpenCommand;
import com.cfd.domain.model.TradeOpenedEvent;
import com.cfd.domain.model.TradeOpenedFeedback;
import com.cfd.trading.domain.OpenPosition;
import com.cfd.trading.domain.OpenPositionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

/**
 * 交易应用服务。
 *
 * <p>处理开仓指令的核心业务逻辑：创建持仓、计算保证金（开仓价格×数量÷杠杆），
 * 并通过Outbox模式可靠发布{@code TradeOpenedEvent}和{@code TradeOpenedFeedback}消息。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class TradingApplicationService {

    private final OpenPositionRepository openPositionRepository;
    private final RetryableOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public TradingApplicationService(OpenPositionRepository openPositionRepository,
                                     RetryableOutboxRepository outboxRepository,
                                     ObjectMapper objectMapper) {
        this.openPositionRepository = openPositionRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 处理开仓指令。
     *
     * <p>执行以下步骤：
     * <ol>
     *   <li>幂等性检查：若订单已存在则直接返回</li>
     *   <li>计算保证金：开仓价格 × 数量 ÷ 杠杆</li>
     *   <li>创建并保存持仓对象</li>
     *   <li>写入Outbox：TradeOpenedEvent（通知清算服务）</li>
     *   <li>写入Outbox：TradeOpenedFeedback（反馈订单服务）</li>
     * </ol>
     * </p>
     *
     * @param command 开仓指令
     */
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

        KafkaEnvelope<TradeOpenedEvent> tradeEventEnvelope = new KafkaEnvelope<>(
                UUID.randomUUID().toString(),
                "TradeOpenedEvent",
                "trading-service",
                Instant.now(),
                command.orderId(),
                tradeOpened);
        KafkaEnvelope<TradeOpenedFeedback> feedbackEnvelope = new KafkaEnvelope<>(
                UUID.randomUUID().toString(),
                "TradeOpenedFeedback",
                "trading-service",
                Instant.now(),
                command.orderId(),
                feedback);

        outboxRepository.save(new OutboxMessage(
                tradeEventEnvelope.messageId(),
                Topics.TRADE_OPENED_EVENT,
                command.orderId(),
                toJson(tradeEventEnvelope),
                Instant.now()));

        outboxRepository.save(new OutboxMessage(
                feedbackEnvelope.messageId(),
                Topics.TRADE_OPENED_FEEDBACK,
                command.orderId(),
                toJson(feedbackEnvelope),
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
