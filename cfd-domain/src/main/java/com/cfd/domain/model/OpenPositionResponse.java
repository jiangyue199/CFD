package com.cfd.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record OpenPositionResponse(
        String orderId,
        String userId,
        String symbol,
        BigDecimal openPrice,
        BigDecimal quantity,
        BigDecimal leverage,
        BigDecimal margin,
        BigDecimal floatingPnl,
        PositionStatus status,
        Instant createdAt
) {
}
