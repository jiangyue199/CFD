package com.cfd.common.kafka.outbox;

public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
