package com.cfd.account.api;

import java.math.BigDecimal;

public record AccountBalanceResponse(
        String userId,
        String currency,
        BigDecimal available
) {
}
