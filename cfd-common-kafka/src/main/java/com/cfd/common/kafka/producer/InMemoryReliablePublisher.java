package com.cfd.common.kafka.producer;

import com.cfd.common.kafka.test.InMemoryKafkaBroker;

public class InMemoryReliablePublisher implements ReliableKafkaPublisher {

    private final InMemoryKafkaBroker broker;

    public InMemoryReliablePublisher(InMemoryKafkaBroker broker) {
        this.broker = broker;
    }

    @Override
    public void publish(String topic, String key, String payload) {
        broker.send(topic, key, payload);
    }
}
