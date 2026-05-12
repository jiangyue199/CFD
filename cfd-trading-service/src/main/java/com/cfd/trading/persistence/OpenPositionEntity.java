package com.cfd.trading.persistence;

import java.math.BigDecimal;
import java.time.Instant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 持仓数据库实体类。
 *
 * <p>对应数据库表{@code t_open_position}，使用MyBatis-Plus进行ORM映射。
 * 包含订单ID、用户ID、交易品种、开仓价格、数量、杠杆、保证金、浮动盈亏、
 * 状态及创建时间等字段。</p>
 *
 * @author CFD Platform Team
 */
@TableName("t_open_position")
public class OpenPositionEntity {

    @TableId(value = "order_id", type = IdType.INPUT)
    private String orderId;

    @TableField("user_id")
    private String userId;

    @TableField("symbol")
    private String symbol;

    @TableField("open_price")
    private BigDecimal openPrice;

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("leverage")
    private BigDecimal leverage;

    @TableField("margin")
    private BigDecimal margin;

    @TableField("floating_pnl")
    private BigDecimal floatingPnl;

    @TableField("status")
    private String status;

    @TableField("created_at")
    private Instant createdAt;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getLeverage() {
        return leverage;
    }

    public void setLeverage(BigDecimal leverage) {
        this.leverage = leverage;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public BigDecimal getFloatingPnl() {
        return floatingPnl;
    }

    public void setFloatingPnl(BigDecimal floatingPnl) {
        this.floatingPnl = floatingPnl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
