package com.cfd.marketdata.api;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MarketQuoteRequest(
        @NotBlank String symbol,
        @NotNull @DecimalMin("0") BigDecimal bid,
        @NotNull @DecimalMin("0") BigDecimal ask
) {
}
