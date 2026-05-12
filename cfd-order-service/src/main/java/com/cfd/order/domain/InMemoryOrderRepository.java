package com.cfd.order.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的订单仓储实现。
 *
 * <p>使用 {@link ConcurrentHashMap} 存储订单数据，主要用于单元测试场景。</p>
 *
 * @author CFD Platform Team
 */
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, OrderAggregate> orders = new ConcurrentHashMap<>();

    /** {@inheritDoc} */
    @Override
    public OrderAggregate saveIfAbsent(OrderAggregate order) {
        return orders.computeIfAbsent(order.getOrderId(), key -> order);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<OrderAggregate> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    /** {@inheritDoc} */
    @Override
    public List<OrderAggregate> findAllByUserId(String userId) {
        return orders.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public void update(OrderAggregate order) {
        orders.put(order.getOrderId(), order);
    }
}
