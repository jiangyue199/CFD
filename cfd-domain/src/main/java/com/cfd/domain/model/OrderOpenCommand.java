package com.cfd.domain.model;

import java.math.BigDecimal;

/**
 * 开仓指令 - 由订单服务通过 Kafka 发送给交易服务的异步命令。
 * <p>
 * 当订单通过风控校验后，订单服务将此指令写入 Outbox 表，由 Relay 投递到 Kafka。
 * 交易服务消费后执行开仓逻辑。
 * <p>
 * Command sent via Kafka from Order Service to Trading Service to open a position.
 * After the order passes risk validation, this command is written to the Outbox table
 * and relayed to Kafka for the Trading Service to consume and execute.
 *
 * @author CFD Platform Team
 * @param orderId  订单唯一标识 / Unique order identifier
 * @param userId   用户标识 / User identifier
 * @param symbol   交易品种代码 / Trading instrument symbol (e.g. "EURUSD")
 * @param openPrice 开仓价格 / Opening price
 * @param quantity  交易数量（手数） / Trade quantity (lots)
 * @param leverage  杠杆倍数 / Leverage multiplier
 */
public record OrderOpenCommand(
        String orderId,
        String userId,
        String symbol,
        BigDecimal openPrice,
        BigDecimal quantity,
        BigDecimal leverage
) {
}
