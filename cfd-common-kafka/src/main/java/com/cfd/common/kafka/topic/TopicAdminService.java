package com.cfd.common.kafka.topic;

import java.util.Collection;

public interface TopicAdminService {

    void createTopics(Collection<String> topicNames, int partitions, short replicationFactor);
}
