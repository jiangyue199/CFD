package com.cfd.common.kafka.topic;

import java.util.Collection;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * 基于 Spring {@link KafkaAdmin} 的 Topic 管理服务实现。
 *
 * <p>通过 {@link KafkaAdmin#createOrModifyTopics} 创建或更新 Topic，
 * 若 Topic 已存在则尝试更新配置（具体行为取决于 Broker 端配置）。
 *
 * @author CFD Platform Team
 * @see TopicAdminService
 */
public class SpringKafkaTopicAdminService implements TopicAdminService {

    private final KafkaAdmin kafkaAdmin;

    /**
     * 创建基于 Spring KafkaAdmin 的 Topic 管理服务。
     *
     * @param kafkaAdmin Spring Kafka 管理客户端
     */
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
