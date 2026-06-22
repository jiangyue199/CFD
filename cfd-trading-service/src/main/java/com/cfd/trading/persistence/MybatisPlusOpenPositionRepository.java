package com.cfd.trading.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.domain.model.PositionStatus;
import com.cfd.trading.domain.OpenPosition;
import com.cfd.trading.domain.OpenPositionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 基于MyBatis-Plus的持仓仓储实现。
 *
 * <p>使用MySQL数据库持久化持仓数据，实现{@link OpenPositionRepository}接口。
 * 提供幂等保存、按订单ID查询、按用户ID查询及计数功能。</p>
 *
 * @author CFD Platform Team
 */
@Repository
public class MybatisPlusOpenPositionRepository implements OpenPositionRepository {

    private final OpenPositionDbMapper mapper;

    public MybatisPlusOpenPositionRepository(OpenPositionDbMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public OpenPosition saveIfAbsent(OpenPosition position) {
        OpenPositionEntity existing = mapper.selectById(position.getOrderId());
        if (existing != null) {
            return toDomain(existing);
        }
        mapper.insert(toEntity(position));
        return position;
    }

    @Override
    public Optional<OpenPosition> findByOrderId(String orderId) {
        return Optional.ofNullable(mapper.selectById(orderId)).map(this::toDomain);
    }

    @Override
    public List<OpenPosition> findByUserId(String userId) {
        return mapper.selectList(new LambdaQueryWrapper<OpenPositionEntity>()
                        .eq(OpenPositionEntity::getUserId, userId))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public int count() {
        return Math.toIntExact(mapper.selectCount(new LambdaQueryWrapper<>()));
    }

    private OpenPositionEntity toEntity(OpenPosition position) {
        OpenPositionEntity entity = new OpenPositionEntity();
        entity.setOrderId(position.getOrderId());
        entity.setUserId(position.getUserId());
        entity.setSymbol(position.getSymbol());
        entity.setOpenPrice(position.getOpenPrice());
        entity.setQuantity(position.getQuantity());
        entity.setLeverage(position.getLeverage());
        entity.setMargin(position.getMargin());
        entity.setFloatingPnl(position.getFloatingPnl());
        entity.setCreatedAt(position.getCreatedAt());
        entity.setStatus(position.getStatus().name());
        return entity;
    }

    private OpenPosition toDomain(OpenPositionEntity entity) {
        return OpenPosition.restore(
                entity.getOrderId(),
                entity.getUserId(),
                entity.getSymbol(),
                entity.getOpenPrice(),
                entity.getQuantity(),
                entity.getLeverage(),
                entity.getMargin(),
                entity.getFloatingPnl(),
                entity.getCreatedAt(),
                PositionStatus.valueOf(entity.getStatus()));
    }
}
