package com.cfd.common.kafka.outbox;

import com.cfd.common.kafka.producer.ReliableKafkaPublisher;

public class OutboxRelayService {

    private final RetryableOutboxRepository outboxRepository;
    private final ReliableKafkaPublisher reliableKafkaPublisher;
    private final int defaultBatchSize;

    public OutboxRelayService(RetryableOutboxRepository outboxRepository, ReliableKafkaPublisher reliableKafkaPublisher) {
        this(outboxRepository, reliableKafkaPublisher, 200);
    }

    public OutboxRelayService(RetryableOutboxRepository outboxRepository,
                              ReliableKafkaPublisher reliableKafkaPublisher,
                              int defaultBatchSize) {
        this.outboxRepository = outboxRepository;
        this.reliableKafkaPublisher = reliableKafkaPublisher;
        this.defaultBatchSize = defaultBatchSize;
    }

    public int flushDefaultBatch() {
        return flushPending(defaultBatchSize);
    }

    public int flushPending(int maxBatch) {
        int published = 0;
        for (OutboxMessage message : outboxRepository.listPending(maxBatch)) {
            try {
                reliableKafkaPublisher.publish(message.getTopic(), message.getKey(), message.getPayload());
                outboxRepository.markPublished(message.getId());
                published++;
            } catch (RuntimeException ex) {
                outboxRepository.markFailed(message.getId(), ex.getMessage());
            }
        }
        return published;
    }
}
