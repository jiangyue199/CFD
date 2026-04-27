package com.cfd.common.kafka.outbox.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cfd.common.kafka.outbox.OutboxMessage;
import com.cfd.common.kafka.outbox.OutboxStatus;
import com.cfd.common.kafka.outbox.RetryableOutboxRepository;

@Repository
public class MybatisPlusOutboxRepository implements RetryableOutboxRepository {

    private final OutboxMessageDbMapper mapper;

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
