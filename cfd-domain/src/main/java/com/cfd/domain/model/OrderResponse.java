package com.cfd.domain.model;

import java.time.Instant;

/**
 * 订单状态响应 - 客户端查询订单时返回的 HTTP 响应体。
 * <p>
 * 包含订单的基本信息及当前处理状态。
 * <p>
 * HTTP response returned to the client when querying order status.
 * Contains basic order information and the current processing status.
 *
 * @author CFD Platform Team
 * @param orderId   订单唯一标识 / Unique order identifier
 * @param userId    用户标识 / User identifier
 * @param symbol    交易品种代码 / Trading instrument symbol
 * @param status    订单当前状态 / Current order status
 * @param createdAt 订单创建时间 / Order creation timestamp
 */
public record OrderResponse(
        String orderId,
        String userId,
        String symbol,
        OrderStatus status,
        Instant createdAt
) {
}
