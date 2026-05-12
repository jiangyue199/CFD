package com.cfd.common.kafka.outbox;

import com.cfd.common.kafka.producer.ReliableKafkaPublisher;

/**
 * 发件箱中继服务（Outbox Relay）。
 *
 * <p>负责轮询发件箱中的待发布消息，逐条通过 {@link ReliableKafkaPublisher} 发送到 Kafka。
 * 发送成功后将消息标记为 {@link OutboxStatus#PUBLISHED}，失败则标记为 {@link OutboxStatus#FAILED}
 * 并记录错误信息，等待下次轮询时重试。
 *
 * <p>该服务通常由 {@link OutboxRelayScheduler} 定时驱动，也可手动调用以立即刷新。
 *
 * @author CFD Platform Team
 * @see OutboxRelayScheduler
 * @see RetryableOutboxRepository
 * @see ReliableKafkaPublisher
 */
public class OutboxRelayService {

    private final RetryableOutboxRepository outboxRepository;
    private final ReliableKafkaPublisher reliableKafkaPublisher;
    private final int defaultBatchSize;

    /**
     * 创建发件箱中继服务，使用默认批次大小（200）。
     *
     * @param outboxRepository       发件箱仓库
     * @param reliableKafkaPublisher 可靠消息发布器
     */
    public OutboxRelayService(RetryableOutboxRepository outboxRepository, ReliableKafkaPublisher reliableKafkaPublisher) {
        this(outboxRepository, reliableKafkaPublisher, 200);
    }

    /**
     * 创建发件箱中继服务，指定批次大小。
     *
     * @param outboxRepository       发件箱仓库
     * @param reliableKafkaPublisher 可靠消息发布器
     * @param defaultBatchSize       默认每次轮询处理的最大消息数
     */
    public OutboxRelayService(RetryableOutboxRepository outboxRepository,
                              ReliableKafkaPublisher reliableKafkaPublisher,
                              int defaultBatchSize) {
        this.outboxRepository = outboxRepository;
        this.reliableKafkaPublisher = reliableKafkaPublisher;
        this.defaultBatchSize = defaultBatchSize;
    }

    /**
     * 使用默认批次大小刷新待发布消息。
     *
     * @return 本次成功发布的消息数量
     */
    public int flushDefaultBatch() {
        return flushPending(defaultBatchSize);
    }

    /**
     * 刷新待发布消息。
     *
     * <p>从发件箱中查询最多 {@code maxBatch} 条待发布消息，逐条发送到 Kafka。
     * 发送成功标记为 {@link OutboxStatus#PUBLISHED}，失败标记为 {@link OutboxStatus#FAILED}。
     *
     * @param maxBatch 本次最多处理的消息数量
     * @return 本次成功发布的消息数量
     */
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
