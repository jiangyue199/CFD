package com.cfd.order.domain;

import java.time.Instant;

import com.cfd.domain.model.OrderStatus;

public class OrderAggregate {

    private final String orderId;
    private final String userId;
    private final String symbol;
    private final Instant createdAt;
    private OrderStatus status;

    public OrderAggregate(String orderId, String userId, String symbol, OrderStatus status) {
        this.orderId = orderId;
        this.userId = userId;
        this.symbol = symbol;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
