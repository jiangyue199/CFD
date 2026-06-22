package com.cfd.common.kafka.outbox.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cfd.common.kafka.outbox.OutboxMessage;
import com.cfd.common.kafka.outbox.OutboxStatus;
import com.cfd.common.kafka.outbox.RetryableOutboxRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 基于 MyBatis-Plus 的 MySQL 发件箱仓库实现。
 *
 * <p>实现 {@link RetryableOutboxRepository} 接口，通过 {@link OutboxMessageDbMapper}
 * 将发件箱消息持久化到 MySQL 的 {@code cfd_outbox_message} 表。
 * 支持待发布消息查询、状态更新以及已发布消息的批量清理。
 *
 * <p>查询待发布消息时，同时包含 {@link OutboxStatus#PENDING} 和 {@link OutboxStatus#FAILED}
 * 状态的记录，以支持失败消息的自动重试。
 *
 * @author CFD Platform Team
 * @see RetryableOutboxRepository
 * @see OutboxMessageDbMapper
 * @see OutboxMessageEntity
 */
@Repository
public class MybatisPlusOutboxRepository implements RetryableOutboxRepository {

    private final OutboxMessageDbMapper mapper;

    /**
     * 创建 MyBatis-Plus 发件箱仓库实例。
     *
     * @param mapper 发件箱消息 Mapper
     */
    public MybatisPlusOutboxRepository(OutboxMessageDbMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(OutboxMessage message) {
        mapper.insert(OutboxMessageEntity.fromDomain(message));
    }

    @Override
    public List<OutboxMessage> listPending(int maxSize) {
        return mapper.selectList(new LambdaQueryWrapper<OutboxMessageEntity>()
                        .in(OutboxMessageEntity::getStatus, OutboxStatus.PENDING.name(), OutboxStatus.FAILED.name())
                        .orderByAsc(OutboxMessageEntity::getCreatedAt)
                        .last("LIMIT " + maxSize))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void markPublished(String id) {
        mapper.update(null, new LambdaUpdateWrapper<OutboxMessageEntity>()
                .eq(OutboxMessageEntity::getId, id)
                .set(OutboxMessageEntity::getStatus, OutboxStatus.PUBLISHED.name())
                .set(OutboxMessageEntity::getErrorMessage, null));
    }

    @Override
    public void markFailed(String id, String error) {
        mapper.update(null, new LambdaUpdateWrapper<OutboxMessageEntity>()
                .eq(OutboxMessageEntity::getId, id)
                .set(OutboxMessageEntity::getStatus, OutboxStatus.FAILED.name())
                .set(OutboxMessageEntity::getErrorMessage, error));
    }

    @Override
    public int deletePublished() {
        return mapper.delete(new LambdaQueryWrapper<OutboxMessageEntity>()
                .eq(OutboxMessageEntity::getStatus, OutboxStatus.PUBLISHED.name()));
    }

    private OutboxMessage toDomain(OutboxMessageEntity entity) {
        return entity.toDomain();
    }
}
