package com.cfd.domain.model;

import java.math.BigDecimal;

public record RiskCheckRequest(
        String userId,
        String symbol,
        BigDecimal quantity,
        BigDecimal leverage
) {
}
