package com.cfd.domain.model;

import java.math.BigDecimal;

public record TradeOpenedEvent(
        String orderId,
        String userId,
        String symbol,
        BigDecimal openPrice,
        BigDecimal quantity,
        BigDecimal leverage,
        BigDecimal margin,
        BigDecimal floatingPnl
) {
}
