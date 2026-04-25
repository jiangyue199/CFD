package com.cfd.common.kafka.outbox;

public interface RetryableOutboxRepository extends OutboxRepository {

    int deletePublished();
}
