package com.cfd.common.kafka.idempotent;

import java.util.function.Consumer;

public class IdempotentConsumerExecutor {

    private final ConsumerDedupStore dedupStore;

    public IdempotentConsumerExecutor(ConsumerDedupStore dedupStore) {
        this.dedupStore = dedupStore;
    }

    public <T> boolean execute(String consumerGroup, String messageId, T payload, Consumer<T> consumer) {
        if (!dedupStore.markIfNew(consumerGroup, messageId)) {
            return false;
        }
        consumer.accept(payload);
        return true;
    }
}
