package com.cfd.domain.model;

import java.math.BigDecimal;

/**
 * 风控校验请求 - 订单服务发送给风控服务的校验请求。
 * <p>
 * 订单创建后，订单服务通过 HTTP 调用风控服务，传递此请求以验证用户是否满足
 * 保证金、杠杆限制等风控规则。
 * <p>
 * Risk validation request sent from Order Service to Risk Service via HTTP.
 * After an order is created, the Order Service calls the Risk Service with this
 * request to verify that the user meets margin, leverage, and other risk rules.
 *
 * @author CFD Platform Team
 * @param userId   用户标识 / User identifier
 * @param symbol   交易品种代码 / Trading instrument symbol
 * @param quantity 交易数量（手数） / Trade quantity (lots)
 * @param leverage 杠杆倍数 / Leverage multiplier
 */
public record RiskCheckRequest(
        String userId,
        String symbol,
        BigDecimal quantity,
        BigDecimal leverage
) {
}
