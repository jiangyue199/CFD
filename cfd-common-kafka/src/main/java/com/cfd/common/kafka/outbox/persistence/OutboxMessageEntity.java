package com.cfd.common.kafka.outbox.persistence;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cfd.common.kafka.outbox.OutboxMessage;
import com.cfd.common.kafka.outbox.OutboxStatus;

@TableName("cfd_outbox_message")
public class OutboxMessageEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("topic")
    private String topic;

    @TableField("message_key")
    private String messageKey;

    @TableField("payload")
    private String payload;

    @TableField("status")
    private String status;

    @TableField("error_message")
    private String errorMessage;

    @TableField("created_at")
    private Instant createdAt;

    public static OutboxMessageEntity fromDomain(OutboxMessage message) {
        OutboxMessageEntity entity = new OutboxMessageEntity();
        entity.id = message.getId();
        entity.topic = message.getTopic();
        entity.messageKey = message.getKey();
        entity.payload = message.getPayload();
        entity.status = message.getStatus().name();
        entity.errorMessage = message.getError();
        entity.createdAt = message.getCreatedAt();
        return entity;
    }

    public OutboxMessage toDomain() {
        OutboxMessage message = new OutboxMessage(id, topic, messageKey, payload, createdAt);
        if (OutboxStatus.PUBLISHED.name().equals(status)) {
            message.markPublished();
        } else if (OutboxStatus.FAILED.name().equals(status)) {
            message.markFailed(errorMessage);
        }
        return message;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
