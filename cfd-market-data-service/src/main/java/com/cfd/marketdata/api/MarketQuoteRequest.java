package com.cfd.marketdata.api;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 市场报价请求 DTO。
 *
 * <p>用于接收市场报价的更新请求参数。</p>
 *
 * @param symbol 交易品种代码，不能为空
 * @param bid    买入价，不能为空且不能为负
 * @param ask    卖出价，不能为空且不能为负
 * @author CFD Platform Team
 */
public record MarketQuoteRequest(
        @NotBlank String symbol,
        @NotNull @DecimalMin("0") BigDecimal bid,
        @NotNull @DecimalMin("0") BigDecimal ask
) {
}
