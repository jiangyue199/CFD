package com.cfd.order.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.domain.model.OrderStatus;
import com.cfd.order.domain.OrderAggregate;
import com.cfd.order.domain.OrderRepository;

/**
 * 基于 MyBatis-Plus 的订单仓储实现。
 *
 * <p>通过 {@link OrderDbMapper} 操作 MySQL 数据库，实现订单聚合的持久化。</p>
 *
 * @author CFD Platform Team
 */
@Repository
public class MybatisPlusOrderRepository implements OrderRepository {

    private final OrderDbMapper orderDbMapper;

    /**
     * 构造 MyBatis-Plus 订单仓储。
     *
     * @param orderDbMapper 订单数据库 Mapper
     */
    public MybatisPlusOrderRepository(OrderDbMapper orderDbMapper) {
        this.orderDbMapper = orderDbMapper;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized OrderAggregate saveIfAbsent(OrderAggregate order) {
        OrderEntity existing = orderDbMapper.selectById(order.getOrderId());
        if (existing != null) {
            return toDomain(existing);
        }
        orderDbMapper.insert(toEntity(order));
        return order;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<OrderAggregate> findById(String orderId) {
        return Optional.ofNullable(orderDbMapper.selectById(orderId)).map(this::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public List<OrderAggregate> findAllByUserId(String userId) {
        return orderDbMapper.selectList(new LambdaQueryWrapper<OrderEntity>()
                        .eq(OrderEntity::getUserId, userId))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public void update(OrderAggregate order) {
        orderDbMapper.updateById(toEntity(order));
    }

    /**
     * 将持久化实体转换为领域聚合。
     *
     * @param entity 订单持久化实体
     * @return 订单聚合
     */
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

    /**
     * 将领域聚合转换为持久化实体。
     *
     * @param aggregate 订单聚合
     * @return 订单持久化实体
     */
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
