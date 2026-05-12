package com.cfd.domain.kafka;

/**
 * Kafka 主题名称常量 - 定义 CFD 平台各微服务间 Kafka 通信使用的 Topic 名称。
 * <p>
 * Kafka topic name constants used for inter-service messaging across CFD microservices.
 *
 * @author CFD Platform Team
 */
public final class Topics {

    /** 开仓指令主题 - 订单服务 → 交易服务 / Open position command: Order Service → Trading Service */
    public static final String ORDER_OPEN_COMMAND = "cfd.order.open.command";

    /** 成交事件主题 - 交易服务 → 清算服务 / Trade opened event: Trading Service → Clearing Service */
    public static final String TRADE_OPENED_EVENT = "cfd.trade.opened.event";

    /** 成交反馈主题 - 交易服务 → 订单服务 / Trade opened feedback: Trading Service → Order Service */
    public static final String TRADE_OPENED_FEEDBACK = "cfd.trade.opened.feedback";

    private Topics() {
    }
}
