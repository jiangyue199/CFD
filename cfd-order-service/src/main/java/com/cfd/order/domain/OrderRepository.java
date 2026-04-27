package com.cfd.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    OrderAggregate saveIfAbsent(OrderAggregate order);

    Optional<OrderAggregate> findById(String orderId);

    List<OrderAggregate> findAllByUserId(String userId);

    void update(OrderAggregate order);
}
