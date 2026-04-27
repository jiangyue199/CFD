package com.cfd.domain.model;

import java.time.Instant;

public record OrderResponse(
        String orderId,
        String userId,
        String symbol,
        OrderStatus status,
        Instant createdAt
) {
}
