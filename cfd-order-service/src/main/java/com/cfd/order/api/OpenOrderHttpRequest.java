package com.cfd.order.api;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OpenOrderHttpRequest(
        @NotBlank String orderId,
        @NotBlank String userId,
        @NotBlank String symbol,
        @NotNull @DecimalMin("0.0000001") BigDecimal openPrice,
        @NotNull @DecimalMin("0.0000001") BigDecimal quantity,
        @NotNull @DecimalMin("1") BigDecimal leverage
) {
}
