package com.cfd.order.domain;

import com.cfd.domain.model.OrderResponse;

/**
 * 订单对象映射工具类。
 *
 * <p>提供订单聚合到响应 DTO 的转换方法。</p>
 *
 * @author CFD Platform Team
 */
public final class OrderMapper {

    private OrderMapper() {
    }

    /**
     * 将订单聚合转换为订单响应 DTO。
     *
     * @param order 订单聚合
     * @return 订单响应 DTO
     */
    public static OrderResponse toResponse(OrderAggregate order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getUserId(),
                order.getSymbol(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
