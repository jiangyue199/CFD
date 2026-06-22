package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 用户状态实体类。
 *
 * <p>对应数据库 user_status 表，存储用户账户状态信息。
 * 支持状态包括：ACTIVE(正常)、FROZEN(冻结)、WHITELIST(白名单)。</p>
 *
 * @author CFD Platform Team
 */
@TableName("user_status")
public class UserStatusEntity {

    @TableId("user_id")
    private String userId;

    @TableField("status")
    private String status;

    @TableField("frozen_reason")
    private String frozenReason;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFrozenReason() { return frozenReason; }
    public void setFrozenReason(String frozenReason) { this.frozenReason = frozenReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
