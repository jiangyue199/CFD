package com.cfd.common.kafka.outbox;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
}
