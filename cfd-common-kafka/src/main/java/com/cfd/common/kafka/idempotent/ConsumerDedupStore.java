package com.cfd.common.kafka.idempotent;

/**
 * 消费者消息去重存储接口。
 *
 * <p>用于跟踪每个消费组已处理的消息 ID，配合 {@link IdempotentConsumerExecutor}
 * 实现 Kafka 消费端的幂等性保障。典型处理流程：
 * <ol>
 *     <li>{@link #tryStartProcessing} — 尝试锁定消息，若已处理或处理中则返回 {@code false}</li>
 *     <li>执行业务逻辑</li>
 *     <li>{@link #markProcessed} — 业务成功，标记为已处理</li>
 *     <li>{@link #clearProcessing} — 业务失败，清除处理中标记以允许重试</li>
 * </ol>
 *
 * @author CFD Platform Team
 * @see IdempotentConsumerExecutor
 * @see InMemoryConsumerDedupStore
 * @see InMemoryAndExpiryConsumerDedupStore
 */
public interface ConsumerDedupStore {

    /**
     * 尝试开始处理指定消息。
     *
     * <p>如果该消息尚未被处理或正在处理中，则标记为"处理中"并返回 {@code true}；
     * 否则返回 {@code false}，表示消息已处理或正在被其他线程处理。
     *
     * @param consumerGroup 消费组名称
     * @param messageId     消息唯一标识
     * @return 若成功获得处理权返回 {@code true}，否则返回 {@code false}
     */
    boolean tryStartProcessing(String consumerGroup, String messageId);

    /**
     * 将消息标记为已成功处理。
     *
     * <p>在业务逻辑执行成功后调用，后续相同消息的 {@link #tryStartProcessing} 将返回 {@code false}。
     *
     * @param consumerGroup 消费组名称
     * @param messageId     消息唯一标识
     */
    void markProcessed(String consumerGroup, String messageId);

    /**
     * 清除消息的"处理中"标记。
     *
     * <p>在业务逻辑执行失败后调用，允许该消息被重新消费和处理。
     *
     * @param consumerGroup 消费组名称
     * @param messageId     消息唯一标识
     */
    void clearProcessing(String consumerGroup, String messageId);
}
