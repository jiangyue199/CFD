package com.cfd.order.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 开仓订单 HTTP 请求 DTO。
 *
 * <p>封装客户端提交开仓订单时所需的参数，包含 JSR-303 校验注解。</p>
 *
 * @param orderId   订单唯一标识
 * @param userId    用户标识
 * @param symbol    交易品种代码
 * @param openPrice 开仓价格
 * @param quantity  交易数量
 * @param leverage  杠杆倍数
 * @author CFD Platform Team
 */
public record OpenOrderHttpRequest(
        @NotBlank String orderId,
        @NotBlank String userId,
        @NotBlank String symbol,
        @NotNull @DecimalMin("0.0000001") BigDecimal openPrice,
        @NotNull @DecimalMin("0.0000001") BigDecimal quantity,
        @NotNull @DecimalMin("1") BigDecimal leverage
) {
}
