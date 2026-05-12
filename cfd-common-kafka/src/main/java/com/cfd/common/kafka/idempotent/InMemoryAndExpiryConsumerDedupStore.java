package com.cfd.common.kafka.idempotent;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于内存的消费者去重存储，支持 TTL 过期淘汰。
 *
 * <p>使用 {@link ConcurrentHashMap} 按消费组维度存储已处理消息 ID 及其时间戳。
 * 每次调用 {@link #tryStartProcessing} 时会触发过期清理，移除超过 TTL 的记录，
 * 防止内存无限增长。
 *
 * <p>适用于对去重精度要求不高的场景（如开发、测试环境，或单节点部署）。
 * 分布式环境建议使用 Redis 等外部存储实现 {@link ConsumerDedupStore}。
 *
 * @author CFD Platform Team
 * @see ConsumerDedupStore
 * @see InMemoryConsumerDedupStore
 */
public class InMemoryAndExpiryConsumerDedupStore implements ConsumerDedupStore {

    private final ConcurrentMap<String, ConcurrentMap<String, Instant>> dedupByGroup = new ConcurrentHashMap<>();
    private final Duration ttl;

    /**
     * 创建带 TTL 过期的内存去重存储。
     *
     * @param ttl 消息去重记录的存活时间，超过此时间的记录将被清理
     */
    public InMemoryAndExpiryConsumerDedupStore(Duration ttl) {
        this.ttl = ttl;
    }

    @Override
    public boolean tryStartProcessing(String consumerGroup, String messageId) {
        cleanupExpired();
        Instant now = Instant.now();
        ConcurrentMap<String, Instant> groupStore = dedupByGroup.computeIfAbsent(consumerGroup, key -> new ConcurrentHashMap<>());
        Instant previous = groupStore.putIfAbsent(messageId, now);
        return previous == null;
    }

    @Override
    public void markProcessed(String consumerGroup, String messageId) {
        // no-op for in-memory implementation, key already inserted on start.
    }

    @Override
    public void clearProcessing(String consumerGroup, String messageId) {
        ConcurrentMap<String, Instant> groupStore = dedupByGroup.get(consumerGroup);
        if (groupStore != null) {
            groupStore.remove(messageId);
        }
    }

    private void cleanupExpired() {
        Instant threshold = Instant.now().minus(ttl);
        for (Map.Entry<String, ConcurrentMap<String, Instant>> groupEntry : dedupByGroup.entrySet()) {
            ConcurrentMap<String, Instant> groupStore = groupEntry.getValue();
            for (Map.Entry<String, Instant> messageEntry : groupStore.entrySet()) {
                if (messageEntry.getValue().isBefore(threshold)) {
                    groupStore.remove(messageEntry.getKey(), messageEntry.getValue());
                }
            }
        }
    }
}
