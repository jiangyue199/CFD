package com.cfd.domain.model;

import java.math.BigDecimal;

/**
 * 成交事件 - 交易服务开仓成功后发送给清算服务的事件。
 * <p>
 * 交易服务执行开仓后，将此事件通过 Kafka 发布到清算服务，用于更新持仓记录和账户余额。
 * <p>
 * Event published via Kafka from Trading Service to Clearing Service when a position
 * is successfully opened. The Clearing Service uses this to update position records
 * and account balances.
 *
 * @author CFD Platform Team
 * @param orderId     订单唯一标识 / Unique order identifier
 * @param userId      用户标识 / User identifier
 * @param symbol      交易品种代码 / Trading instrument symbol
 * @param openPrice   开仓价格 / Opening price
 * @param quantity    交易数量（手数） / Trade quantity (lots)
 * @param leverage    杠杆倍数 / Leverage multiplier
 * @param margin      占用保证金 / Margin held for this position
 * @param floatingPnl 浮动盈亏（初始为 0） / Floating profit/loss (initially 0)
 */
public record TradeOpenedEvent(
        String orderId,
        String userId,
        String symbol,
        BigDecimal openPrice,
        BigDecimal quantity,
        BigDecimal leverage,
        BigDecimal margin,
        BigDecimal floatingPnl
) {
}
