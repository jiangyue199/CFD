package com.cfd.order.domain;

import com.cfd.domain.model.OrderStatus;

/**
 * 订单领域服务。
 *
 * <p>封装订单状态流转的领域逻辑，包括创建待风控订单、标记风控拒绝、
 * 标记已发送至交易系统、标记已开仓等状态变更操作。</p>
 *
 * @author CFD Platform Team
 */
public class OrderDomainService {

    /**
     * 创建一个处于待风控状态的订单。
     *
     * @param orderId 订单唯一标识
     * @param userId  用户标识
     * @param symbol  交易品种代码
     * @return 新创建的订单聚合
     */
    public OrderAggregate createPendingOrder(String orderId, String userId, String symbol) {
        return new OrderAggregate(orderId, userId, symbol, OrderStatus.PENDING_RISK);
    }

    /**
     * 将订单标记为风控拒绝状态。
     *
     * @param order 订单聚合
     */
    public void markRiskRejected(OrderAggregate order) {
        order.setStatus(OrderStatus.RISK_REJECTED);
    }

    /**
     * 将订单标记为已发送至交易系统状态。
     *
     * @param order 订单聚合
     */
    public void markSentToTrading(OrderAggregate order) {
        order.setStatus(OrderStatus.SENT_TO_TRADING);
    }

    /**
     * 将订单标记为已开仓状态。
     *
     * @param order 订单聚合
     */
    public void markOpened(OrderAggregate order) {
        order.setStatus(OrderStatus.OPENED);
    }

    /**
     * 判断订单状态是否为终态（已开仓或风控拒绝）。
     *
     * @param status 订单状态
     * @return 如果为终态则返回 true
     */
    public boolean isFinalState(OrderStatus status) {
        return status == OrderStatus.OPENED || status == OrderStatus.RISK_REJECTED;
    }
}
