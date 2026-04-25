package com.cfd.order.service;

import java.time.Instant;
import java.util.Optional;
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
import com.cfd.domain.model.OrderOpenRequest;
import com.cfd.domain.model.RiskCheckRequest;
import com.cfd.domain.model.RiskCheckResponse;
import com.cfd.domain.model.TradeOpenedFeedback;
import com.cfd.order.client.RiskFeignClient;
import com.cfd.order.domain.OrderAggregate;
import com.cfd.order.domain.OrderDomainService;
import com.cfd.order.domain.OrderMapper;
import com.cfd.order.domain.OrderRepository;

@Service
public class OrderApplicationService {

    private final RiskFeignClient riskFeignClient;
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OrderApplicationService(RiskFeignClient riskFeignClient,
                                   OrderRepository orderRepository,
                                   OrderDomainService orderDomainService,
                                   OutboxRepository outboxRepository,
                                   ObjectMapper objectMapper) {
        this.riskFeignClient = riskFeignClient;
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public synchronized com.cfd.domain.model.OrderResponse submitOpenOrder(OrderOpenRequest request) {
        Optional<OrderAggregate> existing = orderRepository.findById(request.orderId());
        if (existing.isPresent()) {
            return OrderMapper.toResponse(existing.get());
        }

        OrderAggregate order = orderDomainService.createPendingOrder(request.orderId(), request.userId(), request.symbol());
        orderRepository.saveIfAbsent(order);

        RiskCheckResponse risk = riskFeignClient.checkOpenRisk(new RiskCheckRequest(
                request.userId(), request.symbol(), request.quantity(), request.leverage()));
        if (!risk.allowed()) {
            orderDomainService.markRiskRejected(order);
            orderRepository.update(order);
            return OrderMapper.toResponse(order);
        }

        OrderOpenCommand command = new OrderOpenCommand(
                request.orderId(), request.userId(), request.symbol(),
                request.openPrice(), request.quantity(), request.leverage());
        KafkaEnvelope<OrderOpenCommand> envelope = new KafkaEnvelope<>(
                UUID.randomUUID().toString(),
                "OrderOpenCommand",
                "order-service",
                Instant.now(),
                request.orderId(),
                command
        );

        outboxRepository.save(new OutboxMessage(
                envelope.messageId(),
                Topics.ORDER_OPEN_COMMAND,
                request.orderId(),
                toJson(envelope),
                Instant.now()
        ));

        orderDomainService.markSentToTrading(order);
        orderRepository.update(order);
        return OrderMapper.toResponse(order);
    }

    @Transactional
    public synchronized void onTradeOpened(TradeOpenedFeedback feedback) {
        orderRepository.findById(feedback.orderId()).ifPresent(order -> {
            if (orderDomainService.isFinalState(order.getStatus())) {
                return;
            }
            orderDomainService.markOpened(order);
            orderRepository.update(order);
        });
    }

    public com.cfd.domain.model.OrderResponse getOrder(String orderId) {
        OrderAggregate order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        return OrderMapper.toResponse(order);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize order event", ex);
        }
    }
}
