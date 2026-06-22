package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 持仓保证金信息实体类。
 */
@TableName("position_margin")
public class PositionMarginEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private String orderId;

    @TableField("user_id")
    private String userId;

    @TableField("symbol")
    private String symbol;

    @TableField("direction")
    private String direction;

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("open_price")
    private BigDecimal openPrice;

    @TableField("current_price")
    private BigDecimal currentPrice;

    @TableField("leverage")
    private BigDecimal leverage;

    @TableField("used_margin")
    private BigDecimal usedMargin;

    @TableField("floating_pnl")
    private BigDecimal floatingPnl;

    @TableField("swap")
    private BigDecimal swap;

    @TableField("status")
    private String status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public BigDecimal getLeverage() { return leverage; }
    public void setLeverage(BigDecimal leverage) { this.leverage = leverage; }
    public BigDecimal getUsedMargin() { return usedMargin; }
    public void setUsedMargin(BigDecimal usedMargin) { this.usedMargin = usedMargin; }
    public BigDecimal getFloatingPnl() { return floatingPnl; }
    public void setFloatingPnl(BigDecimal floatingPnl) { this.floatingPnl = floatingPnl; }
    public BigDecimal getSwap() { return swap; }
    public void setSwap(BigDecimal swap) { this.swap = swap; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
