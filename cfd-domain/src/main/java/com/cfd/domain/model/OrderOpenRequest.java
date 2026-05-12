package com.cfd.domain.model;

import java.math.BigDecimal;

/**
 * 开仓请求 - 客户端通过 HTTP 发送给订单服务的开仓申请。
 * <p>
 * 客户端提交此请求后，订单服务会创建订单并发起风控校验流程。
 * <p>
 * HTTP request from the client to the Order Service to open a new position.
 * Upon receiving this request, the Order Service creates an order and initiates
 * the risk validation workflow.
 *
 * @author CFD Platform Team
 * @param orderId  订单唯一标识 / Unique order identifier
 * @param userId   用户标识 / User identifier
 * @param symbol   交易品种代码 / Trading instrument symbol (e.g. "EURUSD")
 * @param openPrice 开仓价格 / Opening price
 * @param quantity  交易数量（手数） / Trade quantity (lots)
 * @param leverage  杠杆倍数 / Leverage multiplier
 */
public record OrderOpenRequest(
        String orderId,
        String userId,
        String symbol,
        BigDecimal openPrice,
        BigDecimal quantity,
        BigDecimal leverage
) {
}
