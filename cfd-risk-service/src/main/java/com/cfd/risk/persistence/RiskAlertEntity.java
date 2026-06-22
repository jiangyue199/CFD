package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 风险告警记录实体类。
 */
@TableName("risk_alert")
public class RiskAlertEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    @TableField("alert_type")
    private String alertType;

    @TableField("alert_level")
    private String alertLevel;

    @TableField("message")
    private String message;

    @TableField("margin_level")
    private BigDecimal marginLevel;

    @TableField("equity")
    private BigDecimal equity;

    @TableField("handled")
    private Boolean handled;

    @TableField("handled_at")
    private LocalDateTime handledAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public String getAlertLevel() { return alertLevel; }
    public void setAlertLevel(String alertLevel) { this.alertLevel = alertLevel; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public BigDecimal getMarginLevel() { return marginLevel; }
    public void setMarginLevel(BigDecimal marginLevel) { this.marginLevel = marginLevel; }
    public BigDecimal getEquity() { return equity; }
    public void setEquity(BigDecimal equity) { this.equity = equity; }
    public Boolean getHandled() { return handled; }
    public void setHandled(Boolean handled) { this.handled = handled; }
    public LocalDateTime getHandledAt() { return handledAt; }
    public void setHandledAt(LocalDateTime handledAt) { this.handledAt = handledAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
