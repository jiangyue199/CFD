package com.cfd.common.kafka.outbox;

public class InMemoryAndRetryOutboxRepository extends InMemoryOutboxRepository implements RetryableOutboxRepository {

    @Override
    public int deletePublished() {
        int before = internalStore().size();
        internalStore().entrySet().removeIf(entry -> entry.getValue().getStatus() == OutboxStatus.PUBLISHED);
        return before - internalStore().size();
    }
}
