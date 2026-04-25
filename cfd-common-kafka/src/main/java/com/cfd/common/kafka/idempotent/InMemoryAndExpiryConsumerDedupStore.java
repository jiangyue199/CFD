package com.cfd.common.kafka.idempotent;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Keeps in-memory dedup keys and periodically evicts expired entries.
 */
public class InMemoryAndExpiryConsumerDedupStore implements ConsumerDedupStore {

    private final ConcurrentMap<String, ConcurrentMap<String, Instant>> dedupByGroup = new ConcurrentHashMap<>();
    private final Duration ttl;

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
