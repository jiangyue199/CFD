package com.cfd.common.kafka.idempotent;

public interface ConsumerDedupStore {

    boolean tryStartProcessing(String consumerGroup, String messageId);

    void markProcessed(String consumerGroup, String messageId);

    void clearProcessing(String consumerGroup, String messageId);
}
