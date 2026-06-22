package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 黑白名单实体类。
 *
 * <p>对应数据库 risk_blacklist 表，存储用户/IP/设备/银行卡的黑白名单信息。
 * 支持自动过期机制，expires_at 为 null 表示永久有效。</p>
 *
 * @author CFD Platform Team
 */
@TableName("risk_blacklist")
public class RiskBlacklistEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("list_type")
    private String listType;

    @TableField("target_type")
    private String targetType;

    @TableField("target_value")
    private String targetValue;

    @TableField("reason")
    private String reason;

    @TableField("expires_at")
    private LocalDateTime expiresAt;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getListType() { return listType; }
    public void setListType(String listType) { this.listType = listType; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public String getTargetValue() { return targetValue; }
    public void setTargetValue(String targetValue) { this.targetValue = targetValue; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
