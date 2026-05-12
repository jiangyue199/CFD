package com.cfd.domain.model;

/**
 * 订单状态枚举 - 表示订单在生命周期中的各个阶段。
 * <p>
 * Order status enum representing the stages in an order's lifecycle.
 *
 * @author CFD Platform Team
 */
public enum OrderStatus {

    /** 待风控审核 - 订单已创建，正在等待风控服务校验 / Pending risk check */
    PENDING_RISK,

    /** 风控拒绝 - 订单未通过风控校验 / Rejected by risk validation */
    RISK_REJECTED,

    /** 已发送交易 - 订单已通过风控，指令已投递至交易服务 / Sent to Trading Service */
    SENT_TO_TRADING,

    /** 已开仓 - 交易服务确认开仓成功 / Position successfully opened */
    OPENED
}
