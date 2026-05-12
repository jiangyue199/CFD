package com.cfd.common.kafka.outbox;

/**
 * 支持已发布消息清理的内存发件箱仓库实现。
 *
 * <p>继承 {@link InMemoryOutboxRepository} 并实现 {@link RetryableOutboxRepository}，
 * 增加 {@link #deletePublished()} 能力。适用于集成测试场景。
 *
 * @author CFD Platform Team
 * @see InMemoryOutboxRepository
 * @see RetryableOutboxRepository
 */
public class InMemoryAndRetryOutboxRepository extends InMemoryOutboxRepository implements RetryableOutboxRepository {

    /** {@inheritDoc} */
    @Override
    public int deletePublished() {
        int before = internalStore().size();
        internalStore().entrySet().removeIf(entry -> entry.getValue().getStatus() == OutboxStatus.PUBLISHED);
        return before - internalStore().size();
    }
}
