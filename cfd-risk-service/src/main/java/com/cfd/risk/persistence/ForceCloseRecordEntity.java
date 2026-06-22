package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 强平记录实体类。
 */
@TableName("force_close_record")
public class ForceCloseRecordEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    @TableField("order_id")
    private String orderId;

    @TableField("symbol")
    private String symbol;

    @TableField("close_reason")
    private String closeReason;

    @TableField("close_price")
    private BigDecimal closePrice;

    @TableField("close_quantity")
    private BigDecimal closeQuantity;

    @TableField("realized_pnl")
    private BigDecimal realizedPnl;

    @TableField("margin_before")
    private BigDecimal marginBefore;

    @TableField("margin_after")
    private BigDecimal marginAfter;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getCloseReason() { return closeReason; }
    public void setCloseReason(String closeReason) { this.closeReason = closeReason; }
    public BigDecimal getClosePrice() { return closePrice; }
    public void setClosePrice(BigDecimal closePrice) { this.closePrice = closePrice; }
    public BigDecimal getCloseQuantity() { return closeQuantity; }
    public void setCloseQuantity(BigDecimal closeQuantity) { this.closeQuantity = closeQuantity; }
    public BigDecimal getRealizedPnl() { return realizedPnl; }
    public void setRealizedPnl(BigDecimal realizedPnl) { this.realizedPnl = realizedPnl; }
    public BigDecimal getMarginBefore() { return marginBefore; }
    public void setMarginBefore(BigDecimal marginBefore) { this.marginBefore = marginBefore; }
    public BigDecimal getMarginAfter() { return marginAfter; }
    public void setMarginAfter(BigDecimal marginAfter) { this.marginAfter = marginAfter; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
