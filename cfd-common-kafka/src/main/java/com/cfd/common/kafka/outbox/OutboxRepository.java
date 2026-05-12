package com.cfd.common.kafka.outbox;

import java.util.List;

/**
 * 发件箱持久化仓库接口。
 *
 * <p>定义发件箱消息的基本 CRUD 操作，供 {@link OutboxRelayService} 在轮询投递时使用。
 * 实现类需保证操作的原子性和线程安全性。
 *
 * @author CFD Platform Team
 * @see OutboxMessage
 * @see RetryableOutboxRepository
 * @see InMemoryOutboxRepository
 */
public interface OutboxRepository {

    /**
     * 保存一条发件箱消息。
     *
     * <p>通常在业务事务中调用，确保消息写入与业务数据变更在同一事务内完成。
     *
     * @param message 待保存的发件箱消息
     */
    void save(OutboxMessage message);

    /**
     * 查询待发布的消息列表。
     *
     * <p>返回状态为 {@link OutboxStatus#PENDING} 或 {@link OutboxStatus#FAILED} 的消息，
     * 按创建时间升序排列，最多返回 {@code maxSize} 条。
     *
     * @param maxSize 最大返回数量
     * @return 待发布的消息列表
     */
    List<OutboxMessage> listPending(int maxSize);

    /**
     * 将指定消息标记为已发布。
     *
     * @param id 消息唯一标识
     */
    void markPublished(String id);

    /**
     * 将指定消息标记为发布失败。
     *
     * @param id    消息唯一标识
     * @param error 失败原因描述
     */
    void markFailed(String id, String error);
}
