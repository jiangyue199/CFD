package com.cfd.common.kafka.topic;

import java.util.Collection;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.KafkaAdmin;

public class SpringKafkaTopicAdminService implements TopicAdminService {

    private final KafkaAdmin kafkaAdmin;

    public SpringKafkaTopicAdminService(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public void createTopics(Collection<String> topicNames, int partitions, short replicationFactor) {
        topicNames.stream()
                .map(name -> new NewTopic(name, partitions, replicationFactor))
                .forEach(kafkaAdmin::createOrModifyTopics);
    }
}
