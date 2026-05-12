package com.cfd.domain.model;

/**
 * 持仓状态枚举 - 表示用户持仓的当前状态。
 * <p>
 * Position status enum representing the current state of a user's position.
 *
 * @author CFD Platform Team
 */
public enum PositionStatus {

    /** 已开仓 - 持仓处于活跃状态 / Position is active and open */
    OPENED,

    /** 已平仓 - 持仓已关闭 / Position has been closed */
    CLOSED
}
