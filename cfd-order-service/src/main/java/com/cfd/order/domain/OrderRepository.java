package com.cfd.order.domain;

import java.util.List;
import java.util.Optional;

/**
 * 订单仓储接口。
 *
 * <p>定义订单聚合的持久化操作契约，包括幂等保存、按ID查询、按用户查询和更新。</p>
 *
 * @author CFD Platform Team
 */
public interface OrderRepository {

    /**
     * 幂等保存订单，若已存在则返回已有记录。
     *
     * @param order 待保存的订单聚合
     * @return 已保存的订单聚合（可能是已存在的记录）
     */
    OrderAggregate saveIfAbsent(OrderAggregate order);

    /**
     * 根据订单ID查询订单。
     *
     * @param orderId 订单唯一标识
     * @return 包含订单聚合的 Optional，未找到时为空
     */
    Optional<OrderAggregate> findById(String orderId);

    /**
     * 查询指定用户的所有订单。
     *
     * @param userId 用户标识
     * @return 订单聚合列表
     */
    List<OrderAggregate> findAllByUserId(String userId);

    /**
     * 更新订单聚合状态。
     *
     * @param order 待更新的订单聚合
     */
    void update(OrderAggregate order);
}
