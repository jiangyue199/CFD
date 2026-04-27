package com.cfd.common.kafka.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class InMemoryKafkaBroker {

    private final Map<String, List<Record>> topicMessages = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<Record>>> subscribers = new ConcurrentHashMap<>();

    public synchronized void createTopic(String topic) {
        topicMessages.computeIfAbsent(topic, t -> new ArrayList<>());
        subscribers.computeIfAbsent(topic, t -> new ArrayList<>());
    }

    public synchronized void subscribe(String topic, Consumer<Record> handler) {
        createTopic(topic);
        subscribers.get(topic).add(handler);
    }

    public synchronized void send(String topic, String key, String payload) {
        createTopic(topic);
        Record record = new Record(topic, key, payload);
        topicMessages.get(topic).add(record);
        for (Consumer<Record> consumer : subscribers.get(topic)) {
            consumer.accept(record);
        }
    }

    public synchronized List<Record> history(String topic) {
        createTopic(topic);
        return List.copyOf(topicMessages.get(topic));
    }

    public record Record(String topic, String key, String payload) {}
}
