package com.cfd.common.kafka.outbox;

import com.cfd.common.kafka.producer.ReliableKafkaPublisher;

public class OutboxRelayService {

    private final OutboxRepository outboxRepository;
    private final ReliableKafkaPublisher reliableKafkaPublisher;

    public OutboxRelayService(OutboxRepository outboxRepository, ReliableKafkaPublisher reliableKafkaPublisher) {
        this.outboxRepository = outboxRepository;
        this.reliableKafkaPublisher = reliableKafkaPublisher;
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
