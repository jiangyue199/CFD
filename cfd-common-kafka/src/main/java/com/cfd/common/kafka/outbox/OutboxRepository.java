package com.cfd.common.kafka.outbox;

import java.util.List;

public interface OutboxRepository {

    void save(OutboxMessage message);

    List<OutboxMessage> listPending(int maxSize);

    void markPublished(String id);

    void markFailed(String id, String error);
}
