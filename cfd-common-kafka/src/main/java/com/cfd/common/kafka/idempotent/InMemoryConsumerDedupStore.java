package com.cfd.common.kafka.idempotent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
