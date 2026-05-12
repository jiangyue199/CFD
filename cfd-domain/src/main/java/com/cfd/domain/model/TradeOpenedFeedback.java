package com.cfd.domain.model;

/**
 * 成交反馈 - 交易服务开仓后通过 Kafka 回传给订单服务的反馈消息。
 * <p>
 * 订单服务消费此消息后将订单状态更新为 OPENED。
 * <p>
 * Feedback message sent via Kafka from Trading Service back to Order Service
 * after a position is opened. The Order Service consumes this to update
 * the order status to OPENED.
 *
 * @author CFD Platform Team
 * @param orderId 订单唯一标识 / Unique order identifier
 * @param message 反馈描述信息 / Feedback description message
 */
public record TradeOpenedFeedback(
        String orderId,
        String message
) {
}
