package com.cfd.order.service;

import com.cfd.common.kafka.message.KafkaEnvelope;
import com.cfd.common.kafka.outbox.OutboxMessage;
import com.cfd.common.kafka.outbox.OutboxRepository;
import com.cfd.domain.kafka.Topics;
import com.cfd.domain.model.*;
import com.cfd.order.client.RiskFeignClient;
import com.cfd.order.domain.OrderAggregate;
import com.cfd.order.domain.OrderDomainService;
import com.cfd.order.domain.OrderMapper;
import com.cfd.order.domain.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * 订单应用服务。
 *
 * <p>编排订单提交的完整流程：接收请求 → 调用风控服务 → 保存订单 →
 * 写入 Outbox → 通过 Kafka 中继至交易系统。同时处理交易反馈更新订单状态。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class OrderApplicationService {

    private final RiskFeignClient riskFeignClient;
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * 构造订单应用服务。
     *
     * @param riskFeignClient    风控 Feign 客户端
     * @param orderRepository    订单仓储
     * @param orderDomainService 订单领域服务
     * @param outboxRepository   Outbox 仓储
     * @param objectMapper       JSON 序列化工具
     */
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

    /**
     * 提交开仓订单。
     *
     * <p>执行幂等检查 → 创建订单 → 风控校验 → 写入 Outbox → 更新状态。
     * 若风控不通过则标记为拒绝状态并直接返回。</p>
     *
     * @param request 开仓订单请求
     * @return 订单响应
     */
    @Transactional
    public synchronized com.cfd.domain.model.OrderResponse submitOpenOrder(OrderOpenRequest request) {
        Optional<OrderAggregate> existing = orderRepository.findById(request.orderId());
        if (existing.isPresent()) {
            return OrderMapper.toResponse(existing.get());
        }

        OrderAggregate order = orderDomainService.createPendingOrder(request.orderId(), request.userId(), request.symbol());
        orderRepository.saveIfAbsent(order);

        RiskCheckResponse risk = riskFeignClient.checkOpenRisk(RiskCheckRequest.minimal(
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

    /**
     * 处理交易开仓反馈。
     *
     * <p>接收交易系统的开仓确认后，将订单状态更新为已开仓。
     * 若订单已处于终态则跳过处理。</p>
     *
     * @param feedback 交易开仓反馈
     */
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

    /**
     * 根据订单ID查询订单。
     *
     * @param orderId 订单唯一标识
     * @return 订单响应
     * @throws IllegalArgumentException 当订单不存在时抛出
     */
    public com.cfd.domain.model.OrderResponse getOrder(String orderId) {
        OrderAggregate order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        return OrderMapper.toResponse(order);
    }

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param value 待序列化对象
     * @return JSON 字符串
     * @throws IllegalStateException 序列化失败时抛出
     */
    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize order event", ex);
        }
    }
}
