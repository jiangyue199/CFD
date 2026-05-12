package com.cfd.common.kafka.outbox;

import java.time.Instant;

/**
 * 发件箱（Outbox）消息领域对象。
 *
 * <p>表示一条待发布到 Kafka 的消息，包含目标 Topic、分区键、载荷以及当前投递状态。
 * 作为事务性发件箱模式（Transactional Outbox Pattern）的核心实体，其生命周期为：
 * <ol>
 *     <li>业务事务中创建，初始状态为 {@link OutboxStatus#PENDING}</li>
 *     <li>{@link OutboxRelayService} 轮询并发布到 Kafka，成功后标记为 {@link OutboxStatus#PUBLISHED}</li>
 *     <li>发布失败则标记为 {@link OutboxStatus#FAILED}，记录错误信息以便排查和重试</li>
 * </ol>
 *
 * @author CFD Platform Team
 * @see OutboxStatus
 * @see OutboxRepository
 * @see OutboxRelayService
 */
public class OutboxMessage {

    private final String id;
    private final String topic;
    private final String key;
    private final String payload;
    private final Instant createdAt;
    private OutboxStatus status;
    private String error;

    /**
     * 创建一条发件箱消息，初始状态为 {@link OutboxStatus#PENDING}。
     *
     * @param id        消息唯一标识
     * @param topic     目标 Kafka Topic
     * @param key       消息分区键
     * @param payload   消息载荷（JSON 字符串）
     * @param createdAt 创建时间
     */
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

    /**
     * 将消息标记为已发布。
     *
     * <p>状态变更为 {@link OutboxStatus#PUBLISHED}，同时清除错误信息。
     */
    public void markPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.error = null;
    }

    /**
     * 将消息标记为发布失败。
     *
     * @param error 失败原因描述
     */
    public void markFailed(String error) {
        this.status = OutboxStatus.FAILED;
        this.error = error;
    }
}
