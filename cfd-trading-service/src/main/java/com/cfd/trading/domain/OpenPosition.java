package com.cfd.trading.domain;

import java.math.BigDecimal;
import java.time.Instant;

import com.cfd.domain.model.PositionStatus;

/**
 * 持仓聚合根。
 *
 * <p>表示一个已开仓的CFD持仓，包含订单ID、用户ID、交易品种、开仓价格、
 * 数量、杠杆、保证金、浮动盈亏、状态及创建时间等核心字段。</p>
 *
 * @author CFD Platform Team
 */
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

    /**
     * 构造新的持仓对象。
     *
     * <p>创建时自动设置状态为OPENED，创建时间为当前时间。</p>
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param symbol 交易品种
     * @param openPrice 开仓价格
     * @param quantity 数量
     * @param leverage 杠杆倍数
     * @param margin 保证金
     * @param floatingPnl 浮动盈亏
     */
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

    /**
     * 从持久化存储中恢复持仓对象。
     *
     * <p>用于从数据库加载已有持仓数据时重建领域对象，保留原始创建时间和状态。</p>
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param symbol 交易品种
     * @param openPrice 开仓价格
     * @param quantity 数量
     * @param leverage 杠杆倍数
     * @param margin 保证金
     * @param floatingPnl 浮动盈亏
     * @param createdAt 创建时间
     * @param status 持仓状态
     * @return 恢复后的持仓对象
     */
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

    /**
     * 关闭当前持仓，将状态设置为CLOSED。
     */
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
