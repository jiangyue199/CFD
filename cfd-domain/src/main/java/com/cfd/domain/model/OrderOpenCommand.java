package com.cfd.domain.model;

import java.math.BigDecimal;

public record OrderOpenCommand(
        String orderId,
        String userId,
        String symbol,
        BigDecimal openPrice,
        BigDecimal quantity,
        BigDecimal leverage
) {
}
