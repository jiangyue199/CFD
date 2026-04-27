package com.cfd.order.domain;

import com.cfd.domain.model.OrderResponse;

public final class OrderMapper {

    private OrderMapper() {
    }

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
