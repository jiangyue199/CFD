package com.cfd.order.domain;

import java.util.List;
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
    public List<OrderAggregate> findAllByUserId(String userId) {
        return orders.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void update(OrderAggregate order) {
        orders.put(order.getOrderId(), order);
    }
}
