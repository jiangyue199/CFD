package com.cfd.common.kafka.idempotent;

public interface ConsumerDedupStore {

    boolean markIfNew(String consumerGroup, String messageId);
}
