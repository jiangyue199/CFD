package com.cfd.domain.model;

import java.math.BigDecimal;

public record AccountBalanceResponse(
        String userId,
        BigDecimal available,
        BigDecimal frozenMargin
) {
}
