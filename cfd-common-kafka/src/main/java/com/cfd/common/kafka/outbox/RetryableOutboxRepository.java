package com.cfd.common.kafka.outbox;

/**
 * 可清理的发件箱仓库接口，扩展 {@link OutboxRepository}。
 *
 * <p>在基本发件箱操作基础上增加已发布消息的清理能力，防止发件箱表无限增长。
 * 生产环境实现应定期调用 {@link #deletePublished()} 进行清理。
 *
 * @author CFD Platform Team
 * @see OutboxRepository
 * @see InMemoryAndRetryOutboxRepository
 * @see com.cfd.common.kafka.outbox.persistence.MybatisPlusOutboxRepository
 */
public interface RetryableOutboxRepository extends OutboxRepository {

    /**
     * 删除所有已发布的消息。
     *
     * <p>用于定期清理发件箱中状态为 {@link OutboxStatus#PUBLISHED} 的历史记录，
     * 避免数据持续积压。
     *
     * @return 被删除的消息数量
     */
    int deletePublished();
}
