package com.cfd.common.kafka.outbox.persistence;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cfd.common.kafka.outbox.OutboxMessage;
import com.cfd.common.kafka.outbox.OutboxStatus;

/**
 * 发件箱消息数据库实体，映射到 {@code cfd_outbox_message} 表。
 *
 * <p>基于 MyBatis-Plus 注解完成 ORM 映射，提供与领域对象 {@link OutboxMessage}
 * 之间的双向转换方法：
 * <ul>
 *     <li>{@link #fromDomain(OutboxMessage)} — 领域对象 → 数据库实体</li>
 *     <li>{@link #toDomain()} — 数据库实体 → 领域对象</li>
 * </ul>
 *
 * @author CFD Platform Team
 * @see OutboxMessage
 * @see OutboxMessageDbMapper
 */
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

    /**
     * 将领域对象转换为数据库实体。
     *
     * @param message 发件箱领域对象
     * @return 对应的数据库实体
     */
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

    /**
     * 将数据库实体转换为领域对象。
     *
     * <p>根据持久化的状态字符串还原 {@link OutboxMessage} 的状态（PUBLISHED / FAILED）。
     *
     * @return 对应的发件箱领域对象
     */
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
