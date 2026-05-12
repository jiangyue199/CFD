package com.cfd.common.kafka.topic;

import java.util.Collection;

/**
 * Kafka Topic 管理服务接口。
 *
 * <p>提供 Topic 的创建操作，用于应用启动时自动创建所需的 Kafka Topic。
 *
 * @author CFD Platform Team
 * @see SpringKafkaTopicAdminService
 */
public interface TopicAdminService {

    /**
     * 批量创建 Kafka Topic。
     *
     * <p>对于已存在的 Topic，实现类应保证幂等（不抛异常）。
     *
     * @param topicNames        要创建的 Topic 名称集合
     * @param partitions        每个 Topic 的分区数
     * @param replicationFactor 每个 Topic 的副本因子
     */
    void createTopics(Collection<String> topicNames, int partitions, short replicationFactor);
}
