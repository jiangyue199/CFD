package com.cfd.common.kafka.producer;

import java.util.concurrent.TimeUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

public class SpringKafkaReliablePublisher implements ReliableKafkaPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public SpringKafkaReliablePublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String topic, String key, String payload) {
        try {
            SendResult<String, String> result = kafkaTemplate.send(topic, key, payload).get(5, TimeUnit.SECONDS);
            if (result.getRecordMetadata() == null) {
                throw new KafkaPublishException("Kafka send acknowledged without metadata", null);
            }
        } catch (Exception ex) {
            throw new KafkaPublishException("Failed to send message to topic " + topic, ex);
        }
    }
}
