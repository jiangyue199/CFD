package com.cfd.trading.domain;

import java.util.List;
import java.util.Optional;

/**
 * 持仓仓储接口。
 *
 * <p>定义持仓聚合根的持久化操作契约，包括幂等保存、按订单ID查询、
 * 按用户ID查询以及计数等操作。</p>
 *
 * @author CFD Platform Team
 */
public interface OpenPositionRepository {

    /**
     * 幂等保存持仓，若已存在则返回已有记录。
     *
     * @param position 待保存的持仓对象
     * @return 已保存或已存在的持仓对象
     */
    OpenPosition saveIfAbsent(OpenPosition position);

    /**
     * 根据订单ID查询持仓。
     *
     * @param orderId 订单ID
     * @return 持仓对象（如果存在）
     */
    Optional<OpenPosition> findByOrderId(String orderId);

    /**
     * 根据用户ID查询该用户所有持仓。
     *
     * @param userId 用户ID
     * @return 持仓列表
     */
    List<OpenPosition> findByUserId(String userId);

    /**
     * 获取持仓总数。
     *
     * @return 持仓记录数量
     */
    int count();
}
