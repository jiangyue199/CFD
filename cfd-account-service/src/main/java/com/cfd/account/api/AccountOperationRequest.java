package com.cfd.account.api;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountOperationRequest(
        @NotBlank String userId,
        @NotBlank String currency,
        @NotNull @DecimalMin("0.0000001") BigDecimal amount
) {
}
