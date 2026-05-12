package com.cfd.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 持仓查询响应 - 客户端查询持仓详情时返回的 HTTP 响应体。
 * <p>
 * 包含仓位的完整信息，包括保证金、浮动盈亏和当前状态。
 * <p>
 * HTTP response returned to the client when querying position details.
 * Contains full position information including margin, floating P&amp;L, and current status.
 *
 * @author CFD Platform Team
 * @param orderId     订单唯一标识 / Unique order identifier
 * @param userId      用户标识 / User identifier
 * @param symbol      交易品种代码 / Trading instrument symbol
 * @param openPrice   开仓价格 / Opening price
 * @param quantity    交易数量（手数） / Trade quantity (lots)
 * @param leverage    杠杆倍数 / Leverage multiplier
 * @param margin      占用保证金 / Margin held for this position
 * @param floatingPnl 浮动盈亏 / Floating profit/loss
 * @param status      持仓状态 / Position status (OPENED or CLOSED)
 * @param createdAt   持仓创建时间 / Position creation timestamp
 */
public record OpenPositionResponse(
        String orderId,
        String userId,
        String symbol,
        BigDecimal openPrice,
        BigDecimal quantity,
        BigDecimal leverage,
        BigDecimal margin,
        BigDecimal floatingPnl,
        PositionStatus status,
        Instant createdAt
) {
}
