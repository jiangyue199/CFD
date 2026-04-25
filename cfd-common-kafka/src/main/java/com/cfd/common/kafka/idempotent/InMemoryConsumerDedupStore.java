package com.cfd.common.kafka.idempotent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryConsumerDedupStore implements ConsumerDedupStore {

    private final ConcurrentHashMap<String, Set<String>> processedByGroup = new ConcurrentHashMap<>();

    @Override
    public boolean markIfNew(String consumerGroup, String messageId) {
        Set<String> processed = processedByGroup.computeIfAbsent(consumerGroup, key -> ConcurrentHashMap.newKeySet());
        return processed.add(messageId);
    }
}
