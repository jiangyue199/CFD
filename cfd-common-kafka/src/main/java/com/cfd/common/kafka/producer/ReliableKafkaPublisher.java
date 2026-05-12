package com.cfd.common.kafka.producer;

/**
 * 可靠 Kafka 消息发布器接口。
 *
 * <p>定义统一的消息发布契约，实现类需保证消息被可靠投递到 Kafka。
 * 若发布失败应抛出 {@link KafkaPublishException}。
 *
 * @author CFD Platform Team
 * @see SpringKafkaReliablePublisher
 * @see InMemoryReliablePublisher
 * @see KafkaPublishException
 */
public interface ReliableKafkaPublisher {

    /**
     * 将消息发布到指定的 Kafka Topic。
     *
     * @param topic   目标 Topic 名称
     * @param key     消息分区键，相同 key 的消息将路由到同一分区以保证顺序性
     * @param payload 消息载荷（通常为 JSON 字符串）
     * @throws KafkaPublishException 发布失败时抛出
     */
    void publish(String topic, String key, String payload);
}
