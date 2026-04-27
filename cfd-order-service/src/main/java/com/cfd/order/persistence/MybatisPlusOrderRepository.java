package com.cfd.order.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.domain.model.OrderStatus;
import com.cfd.order.domain.OrderAggregate;
import com.cfd.order.domain.OrderRepository;

@Repository
public class MybatisPlusOrderRepository implements OrderRepository {

    private final OrderDbMapper orderDbMapper;

    public MybatisPlusOrderRepository(OrderDbMapper orderDbMapper) {
        this.orderDbMapper = orderDbMapper;
    }

    @Override
    public synchronized OrderAggregate saveIfAbsent(OrderAggregate order) {
        OrderEntity existing = orderDbMapper.selectById(order.getOrderId());
        if (existing != null) {
            return toDomain(existing);
        }
        orderDbMapper.insert(toEntity(order));
        return order;
    }

    @Override
    public Optional<OrderAggregate> findById(String orderId) {
        return Optional.ofNullable(orderDbMapper.selectById(orderId)).map(this::toDomain);
    }

    @Override
    public List<OrderAggregate> findAllByUserId(String userId) {
        return orderDbMapper.selectList(new LambdaQueryWrapper<OrderEntity>()
                        .eq(OrderEntity::getUserId, userId))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void update(OrderAggregate order) {
        orderDbMapper.updateById(toEntity(order));
    }

    private OrderAggregate toDomain(OrderEntity entity) {
        return new OrderAggregate(
                entity.getOrderId(),
                entity.getUserId(),
                entity.getSymbol(),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getStatusReason(),
                entity.getCreatedAt()
        );
    }

    private OrderEntity toEntity(OrderAggregate aggregate) {
        OrderEntity entity = new OrderEntity();
        entity.setOrderId(aggregate.getOrderId());
        entity.setUserId(aggregate.getUserId());
        entity.setSymbol(aggregate.getSymbol());
        entity.setStatus(aggregate.getStatus().name());
        entity.setStatusReason(aggregate.getStatusReason());
        entity.setCreatedAt(aggregate.getCreatedAt());
        return entity;
    }
}
