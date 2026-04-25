package com.cfd.trading.domain;

import java.util.List;
import java.util.Optional;

public interface OpenPositionRepository {

    OpenPosition saveIfAbsent(OpenPosition position);

    Optional<OpenPosition> findByOrderId(String orderId);

    List<OpenPosition> findByUserId(String userId);

    int count();
}
