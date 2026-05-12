package com.cfd.domain.model;

import java.math.BigDecimal;

/**
 * 账户余额响应 - 客户端查询账户余额时返回的 HTTP 响应体。
 * <p>
 * 包含用户的可用余额和已冻结保证金。
 * <p>
 * HTTP response returned to the client when querying account balance.
 * Contains the user's available balance and frozen margin.
 *
 * @author CFD Platform Team
 * @param userId       用户标识 / User identifier
 * @param available    可用余额 / Available balance for trading
 * @param frozenMargin 已冻结保证金（已开仓位占用） / Frozen margin held by open positions
 */
public record AccountBalanceResponse(
        String userId,
        BigDecimal available,
        BigDecimal frozenMargin
) {
}
