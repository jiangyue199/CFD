package com.cfd.common.kafka.producer;

public interface ReliableKafkaPublisher {

    void publish(String topic, String key, String payload);
}
