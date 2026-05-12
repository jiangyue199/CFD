package com.cfd.trading.domain;

import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的持仓仓储实现。
 *
 * <p>使用{@link ConcurrentHashMap}存储持仓数据，主要用于单元测试场景。
 * 线程安全，支持并发访问。</p>
 *
 * @author CFD Platform Team
 */
public class InMemoryOpenPositionRepository implements OpenPositionRepository {

    private final Map<String, OpenPosition> positions = new ConcurrentHashMap<>();

    @Override
    public OpenPosition saveIfAbsent(OpenPosition position) {
        return positions.computeIfAbsent(position.getOrderId(), key -> position);
    }

    @Override
    public Optional<OpenPosition> findByOrderId(String orderId) {
        return Optional.ofNullable(positions.get(orderId));
    }

    @Override
    public List<OpenPosition> findByUserId(String userId) {
        return positions.values().stream()
                .filter(position -> position.getUserId().equals(userId))
                .toList();
    }

    @Override
    public int count() {
        return positions.size();
    }
}
