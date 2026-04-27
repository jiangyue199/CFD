package com.cfd.common.kafka.outbox;

import java.time.Instant;

public class OutboxMessage {

    private final String id;
    private final String topic;
    private final String key;
    private final String payload;
    private final Instant createdAt;
    private OutboxStatus status;
    private String error;

    public OutboxMessage(String id, String topic, String key, String payload, Instant createdAt) {
        this.id = id;
        this.topic = topic;
        this.key = key;
        this.payload = payload;
        this.createdAt = createdAt;
        this.status = OutboxStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getKey() {
        return key;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public void markPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.error = null;
    }

    public void markFailed(String error) {
        this.status = OutboxStatus.FAILED;
        this.error = error;
    }
}
