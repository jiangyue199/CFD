package com.cfd.order.domain;

import java.util.Optional;

public interface OrderRepository {

    OrderAggregate saveIfAbsent(OrderAggregate order);

    Optional<OrderAggregate> findById(String orderId);

    void update(OrderAggregate order);
}
