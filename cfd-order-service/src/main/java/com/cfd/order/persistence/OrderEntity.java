package com.cfd.order.persistence;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 订单持久化实体类。
 *
 * <p>对应数据库 order_record 表，存储订单的核心字段信息。</p>
 *
 * @author CFD Platform Team
 */
@TableName("order_record")
public class OrderEntity {

    /** 订单唯一标识（主键） */
    @TableId(value = "order_id", type = IdType.INPUT)
    private String orderId;

    /** 用户标识 */
    @TableField("user_id")
    private String userId;

    /** 交易品种代码 */
    @TableField("symbol")
    private String symbol;

    /** 订单状态 */
    @TableField("status")
    private String status;

    /** 状态原因描述 */
    @TableField("status_reason")
    private String statusReason;

    /** 订单创建时间 */
    @TableField("created_at")
    private Instant createdAt;

    /** @return 订单唯一标识 */
    public String getOrderId() {
        return orderId;
    }

    /** @param orderId 订单唯一标识 */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /** @return 用户标识 */
    public String getUserId() {
        return userId;
    }

    /** @param userId 用户标识 */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** @return 交易品种代码 */
    public String getSymbol() {
        return symbol;
    }

    /** @param symbol 交易品种代码 */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /** @return 订单状态 */
    public String getStatus() {
        return status;
    }

    /** @param status 订单状态 */
    public void setStatus(String status) {
        this.status = status;
    }

    /** @return 状态原因描述 */
    public String getStatusReason() {
        return statusReason;
    }

    /** @param statusReason 状态原因描述 */
    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    /** @return 订单创建时间 */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /** @param createdAt 订单创建时间 */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
