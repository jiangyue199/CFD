package com.cfd.account.api;

import java.math.BigDecimal;

/**
 * 账户余额响应 DTO。
 *
 * <p>用于向客户端返回账户余额信息，包含用户ID、币种和可用余额。</p>
 *
 * @param userId    用户ID
 * @param currency  币种代码
 * @param available 可用余额
 * @author CFD Platform Team
 */
public record AccountBalanceResponse(
        String userId,
        String currency,
        BigDecimal available
) {
}
