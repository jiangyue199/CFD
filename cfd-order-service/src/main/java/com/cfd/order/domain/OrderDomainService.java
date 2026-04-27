package com.cfd.order.domain;

import com.cfd.domain.model.OrderStatus;

public class OrderDomainService {

    public OrderAggregate createPendingOrder(String orderId, String userId, String symbol) {
        return new OrderAggregate(orderId, userId, symbol, OrderStatus.PENDING_RISK);
    }

    public void markRiskRejected(OrderAggregate order) {
        order.setStatus(OrderStatus.RISK_REJECTED);
    }

    public void markSentToTrading(OrderAggregate order) {
        order.setStatus(OrderStatus.SENT_TO_TRADING);
    }

    public void markOpened(OrderAggregate order) {
        order.setStatus(OrderStatus.OPENED);
    }

    public boolean isFinalState(OrderStatus status) {
        return status == OrderStatus.OPENED || status == OrderStatus.RISK_REJECTED;
    }
}
