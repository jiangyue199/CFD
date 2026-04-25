package com.cfd.common.kafka.message;

import java.time.Instant;

public record KafkaEnvelope<T>(
        String messageId,
        String eventType,
        String sourceService,
        Instant occurredAt,
        String businessKey,
        T payload
) {
}
