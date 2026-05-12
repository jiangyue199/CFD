package com.cfd.common.kafka.idempotent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的简单消费者去重存储（无过期机制）。
 *
 * <p>分别维护"处理中"和"已处理"两个集合，精确区分消息状态。
 * 由于不带 TTL 过期清理，长期运行时内存会持续增长，因此仅适用于
 * 单元测试和短时运行的集成测试场景。
 *
 * @author CFD Platform Team
 * @see ConsumerDedupStore
 * @see InMemoryAndExpiryConsumerDedupStore
 */
public class InMemoryConsumerDedupStore implements ConsumerDedupStore {

    private final ConcurrentHashMap<String, Set<String>> processedByGroup = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> inFlightByGroup = new ConcurrentHashMap<>();

    @Override
    public boolean tryStartProcessing(String consumerGroup, String messageId) {
        Set<String> processed = processedByGroup.computeIfAbsent(consumerGroup, key -> ConcurrentHashMap.newKeySet());
        if (processed.contains(messageId)) {
            return false;
        }
        Set<String> inFlight = inFlightByGroup.computeIfAbsent(consumerGroup, key -> ConcurrentHashMap.newKeySet());
        return inFlight.add(messageId);
    }

    @Override
    public void markProcessed(String consumerGroup, String messageId) {
        inFlightByGroup.computeIfAbsent(consumerGroup, key -> ConcurrentHashMap.newKeySet()).remove(messageId);
        processedByGroup.computeIfAbsent(consumerGroup, key -> ConcurrentHashMap.newKeySet()).add(messageId);
    }

    @Override
    public void clearProcessing(String consumerGroup, String messageId) {
        inFlightByGroup.computeIfAbsent(consumerGroup, key -> ConcurrentHashMap.newKeySet()).remove(messageId);
    }
}
