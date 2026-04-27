package com.cfd.trading.domain;

import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
