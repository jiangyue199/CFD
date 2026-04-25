package com.cfd.trading.domain;

import java.util.Optional;

public interface OpenPositionRepository {

    OpenPosition saveIfAbsent(OpenPosition position);

    Optional<OpenPosition> findByOrderId(String orderId);
}
