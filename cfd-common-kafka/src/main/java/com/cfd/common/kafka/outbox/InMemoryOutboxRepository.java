package com.cfd.common.kafka.outbox;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的发件箱仓库实现。
 *
 * <p>使用 {@link ConcurrentHashMap} 存储发件箱消息，适用于单元测试场景。
 * 不具备持久化能力，进程重启后数据丢失。
 *
 * <p>子类可通过 {@link #internalStore()} 访问底层存储以扩展功能。
 *
 * @author CFD Platform Team
 * @see OutboxRepository
 * @see InMemoryAndRetryOutboxRepository
 */
public class InMemoryOutboxRepository implements OutboxRepository {

    private final Map<String, OutboxMessage> messages = new ConcurrentHashMap<>();

    @Override
    public void save(OutboxMessage message) {
        messages.put(message.getId(), message);
    }

    @Override
    public List<OutboxMessage> listPending(int maxSize) {
        return messages.values().stream()
                .filter(message -> message.getStatus() != OutboxStatus.PUBLISHED)
                .sorted(Comparator.comparing(OutboxMessage::getCreatedAt))
                .limit(maxSize)
                .toList();
    }

    @Override
    public void markPublished(String id) {
        OutboxMessage message = messages.get(id);
        if (message != null) {
            message.markPublished();
        }
    }

    @Override
    public void markFailed(String id, String error) {
        OutboxMessage message = messages.get(id);
        if (message != null) {
            message.markFailed(error);
        }
    }

    /**
     * 获取底层消息存储，供子类扩展使用。
     *
     * @return 内部消息映射表
     */
    protected Map<String, OutboxMessage> internalStore() {
        return messages;
    }
}
