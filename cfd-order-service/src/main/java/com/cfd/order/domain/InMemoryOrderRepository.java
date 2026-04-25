package com.cfd.order.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, OrderAggregate> orders = new ConcurrentHashMap<>();

    @Override
    public OrderAggregate saveIfAbsent(OrderAggregate order) {
        return orders.computeIfAbsent(order.getOrderId(), key -> order);
    }

    @Override
    public Optional<OrderAggregate> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public void update(OrderAggregate order) {
        orders.put(order.getOrderId(), order);
    }
}
