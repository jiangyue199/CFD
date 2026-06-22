package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户浮动亏损统计实体类。
 */
@TableName("user_floating_loss")
public class UserFloatingLossEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    @TableField("trade_date")
    private LocalDate tradeDate;

    @TableField("start_equity")
    private BigDecimal startEquity;

    @TableField("max_floating_loss")
    private BigDecimal maxFloatingLoss;

    @TableField("current_floating_loss")
    private BigDecimal currentFloatingLoss;

    @TableField("loss_ratio")
    private BigDecimal lossRatio;

    @TableField("alert_triggered")
    private Boolean alertTriggered;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }
    public BigDecimal getStartEquity() { return startEquity; }
    public void setStartEquity(BigDecimal startEquity) { this.startEquity = startEquity; }
    public BigDecimal getMaxFloatingLoss() { return maxFloatingLoss; }
    public void setMaxFloatingLoss(BigDecimal maxFloatingLoss) { this.maxFloatingLoss = maxFloatingLoss; }
    public BigDecimal getCurrentFloatingLoss() { return currentFloatingLoss; }
    public void setCurrentFloatingLoss(BigDecimal currentFloatingLoss) { this.currentFloatingLoss = currentFloatingLoss; }
    public BigDecimal getLossRatio() { return lossRatio; }
    public void setLossRatio(BigDecimal lossRatio) { this.lossRatio = lossRatio; }
    public Boolean getAlertTriggered() { return alertTriggered; }
    public void setAlertTriggered(Boolean alertTriggered) { this.alertTriggered = alertTriggered; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
