package com.cfd.account.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 账户操作请求 DTO。
 *
 * <p>用于入金和出金操作的请求参数，包含用户ID、币种和操作金额。</p>
 *
 * @param userId   用户ID，不能为空
 * @param currency 币种代码，不能为空
 * @param amount   操作金额，必须大于 0.0000001
 * @author CFD Platform Team
 */
public record AccountOperationRequest(
        @NotBlank String userId,
        @NotBlank String currency,
        @NotNull @DecimalMin("0.0000001") BigDecimal amount
) {
}
