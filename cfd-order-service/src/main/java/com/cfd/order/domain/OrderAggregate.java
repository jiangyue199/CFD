package com.cfd.order.domain;

import java.time.Instant;

import com.cfd.domain.model.OrderStatus;

public class OrderAggregate {

    private final String orderId;
    private final String userId;
    private final String symbol;
    private final Instant createdAt;
    private OrderStatus status;
    private String statusReason;

    public OrderAggregate(String orderId, String userId, String symbol, OrderStatus status) {
        this.orderId = orderId;
        this.userId = userId;
        this.symbol = symbol;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public OrderAggregate(String orderId,
                          String userId,
                          String symbol,
                          OrderStatus status,
                          String statusReason,
                          Instant createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.symbol = symbol;
        this.status = status;
        this.statusReason = statusReason;
        this.createdAt = createdAt;
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

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public boolean isOpened() {
        return this.status == OrderStatus.OPENED;
    }
}
