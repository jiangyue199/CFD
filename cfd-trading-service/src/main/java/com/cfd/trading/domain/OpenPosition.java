package com.cfd.trading.domain;

import java.math.BigDecimal;
import java.time.Instant;

import com.cfd.domain.model.PositionStatus;

public class OpenPosition {

    private final String orderId;
    private final String userId;
    private final String symbol;
    private final BigDecimal openPrice;
    private final BigDecimal quantity;
    private final BigDecimal leverage;
    private final BigDecimal margin;
    private final BigDecimal floatingPnl;
    private final Instant createdAt;
    private PositionStatus status;

    public OpenPosition(String orderId,
                        String userId,
                        String symbol,
                        BigDecimal openPrice,
                        BigDecimal quantity,
                        BigDecimal leverage,
                        BigDecimal margin,
                        BigDecimal floatingPnl) {
        this.orderId = orderId;
        this.userId = userId;
        this.symbol = symbol;
        this.openPrice = openPrice;
        this.quantity = quantity;
        this.leverage = leverage;
        this.margin = margin;
        this.floatingPnl = floatingPnl;
        this.createdAt = Instant.now();
        this.status = PositionStatus.OPENED;
    }

    public static OpenPosition restore(String orderId,
                                       String userId,
                                       String symbol,
                                       BigDecimal openPrice,
                                       BigDecimal quantity,
                                       BigDecimal leverage,
                                       BigDecimal margin,
                                       BigDecimal floatingPnl,
                                       Instant createdAt,
                                       PositionStatus status) {
        OpenPosition position = new OpenPosition(orderId, userId, symbol, openPrice, quantity, leverage, margin, floatingPnl);
        position.status = status;
        position.createdAtOverride(createdAt);
        return position;
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

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getLeverage() {
        return leverage;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public BigDecimal getFloatingPnl() {
        return floatingPnl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public PositionStatus getStatus() {
        return status;
    }

    public void close() {
        this.status = PositionStatus.CLOSED;
    }

    private void createdAtOverride(Instant createdAt) {
        try {
            java.lang.reflect.Field field = OpenPosition.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(this, createdAt);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to restore open position createdAt", ex);
        }
    }
}
