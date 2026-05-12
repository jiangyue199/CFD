package com.cfd.common.kafka.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 内存 Kafka Broker 模拟器（测试专用）。
 *
 * <p>提供简化的 Kafka Topic 语义，支持创建主题、发送消息、订阅消息和查询历史记录。
 * 消息发送后会同步通知所有已注册的订阅者。所有操作通过 {@code synchronized} 保证线程安全。
 *
 * <p>适用于单元测试和集成测试场景，配合 {@link com.cfd.common.kafka.producer.InMemoryReliablePublisher}
 * 使用，无需启动真实 Kafka 集群即可验证消息发布和消费逻辑。
 *
 * @author CFD Platform Team
 * @see com.cfd.common.kafka.producer.InMemoryReliablePublisher
 */
public class InMemoryKafkaBroker {

    private final Map<String, List<Record>> topicMessages = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<Record>>> subscribers = new ConcurrentHashMap<>();

    /**
     * 创建主题（如已存在则忽略）。
     *
     * @param topic 主题名称
     */
    public synchronized void createTopic(String topic) {
        topicMessages.computeIfAbsent(topic, t -> new ArrayList<>());
        subscribers.computeIfAbsent(topic, t -> new ArrayList<>());
    }

    /**
     * 订阅指定主题的消息。
     *
     * <p>后续发送到该主题的消息将同步回调 {@code handler}。
     *
     * @param topic   主题名称
     * @param handler 消息处理回调
     */
    public synchronized void subscribe(String topic, Consumer<Record> handler) {
        createTopic(topic);
        subscribers.get(topic).add(handler);
    }

    /**
     * 向指定主题发送一条消息。
     *
     * <p>消息将被记录到历史中，并同步通知该主题的所有订阅者。
     *
     * @param topic   目标主题名称
     * @param key     消息分区键
     * @param payload 消息载荷
     */
    public synchronized void send(String topic, String key, String payload) {
        createTopic(topic);
        Record record = new Record(topic, key, payload);
        topicMessages.get(topic).add(record);
        for (Consumer<Record> consumer : subscribers.get(topic)) {
            consumer.accept(record);
        }
    }

    /**
     * 获取指定主题的消息历史记录（不可变副本）。
     *
     * @param topic 主题名称
     * @return 该主题的所有历史消息列表
     */
    public synchronized List<Record> history(String topic) {
        createTopic(topic);
        return List.copyOf(topicMessages.get(topic));
    }

    /**
     * 模拟的 Kafka 消息记录。
     *
     * @param topic   消息所属主题
     * @param key     消息分区键
     * @param payload 消息载荷
     */
    public record Record(String topic, String key, String payload) {}
}
