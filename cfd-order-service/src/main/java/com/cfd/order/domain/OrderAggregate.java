package com.cfd.order.domain;

import com.cfd.domain.model.OrderStatus;

import java.time.Instant;

/**
 * 订单聚合根。
 *
 * <p>封装订单核心状态，包括订单ID、用户ID、交易品种、订单状态、状态原因及创建时间。
 * 作为领域驱动设计中的聚合根，保证订单状态变更的一致性。</p>
 *
 * @author CFD Platform Team
 */
public class OrderAggregate {

    /** 订单唯一标识 */
    private final String orderId;
    /** 用户标识 */
    private final String userId;
    /** 交易品种代码 */
    private final String symbol;
    /** 订单创建时间 */
    private final Instant createdAt;
    /** 订单当前状态 */
    private OrderStatus status;
    /** 状态原因描述 */
    private String statusReason;

    /**
     * 创建订单聚合（自动设置创建时间为当前时间）。
     *
     * @param orderId 订单唯一标识
     * @param userId  用户标识
     * @param symbol  交易品种代码
     * @param status  订单初始状态
     */
    public OrderAggregate(String orderId, String userId, String symbol, OrderStatus status) {
        this.orderId = orderId;
        this.userId = userId;
        this.symbol = symbol;
        this.status = status;
        this.createdAt = Instant.now();
    }

    /**
     * 全参数构造（用于从持久化层重建聚合）。
     *
     * @param orderId      订单唯一标识
     * @param userId       用户标识
     * @param symbol       交易品种代码
     * @param status       订单状态
     * @param statusReason 状态原因
     * @param createdAt    创建时间
     */
    public OrderAggregate(String orderId,
                          String userId,
                          String symbol,
                          OrderStatus status,
                          String statusReason,
                          Instant createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.symbol = symbol;
        this.status = status;
        this.statusReason = statusReason;
        this.createdAt = createdAt;
    }

    /**
     * 获取订单唯一标识。
     *
     * @return 订单ID
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 获取用户标识。
     *
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 获取交易品种代码。
     *
     * @return 品种代码
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * 获取订单创建时间。
     *
     * @return 创建时间
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * 获取订单当前状态。
     *
     * @return 订单状态
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * 设置订单状态。
     *
     * @param status 新的订单状态
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * 获取状态原因描述。
     *
     * @return 状态原因
     */
    public String getStatusReason() {
        return statusReason;
    }

    /**
     * 设置状态原因描述。
     *
     * @param statusReason 状态原因
     */
    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    /**
     * 判断订单是否已开仓。
     *
     * @return 如果状态为 OPENED 则返回 true
     */
    public boolean isOpened() {
        return this.status == OrderStatus.OPENED;
    }
}
