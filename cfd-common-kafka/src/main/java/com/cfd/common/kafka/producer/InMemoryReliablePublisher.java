package com.cfd.common.kafka.producer;

import com.cfd.common.kafka.test.InMemoryKafkaBroker;

/**
 * 基于内存的可靠消息发布器实现（测试专用）。
 *
 * <p>将消息发送到 {@link InMemoryKafkaBroker} 而非真实 Kafka 集群，
 * 适用于单元测试和集成测试场景，无需启动外部 Kafka 依赖。
 *
 * @author CFD Platform Team
 * @see ReliableKafkaPublisher
 * @see InMemoryKafkaBroker
 */
public class InMemoryReliablePublisher implements ReliableKafkaPublisher {

    private final InMemoryKafkaBroker broker;

    /**
     * 创建基于内存 Broker 的发布器。
     *
     * @param broker 内存 Kafka Broker 模拟器
     */
    public InMemoryReliablePublisher(InMemoryKafkaBroker broker) {
        this.broker = broker;
    }

    @Override
    public void publish(String topic, String key, String payload) {
        broker.send(topic, key, payload);
    }
}
