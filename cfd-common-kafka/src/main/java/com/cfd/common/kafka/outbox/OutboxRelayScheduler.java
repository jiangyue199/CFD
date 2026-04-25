package com.cfd.common.kafka.outbox;

import org.springframework.scheduling.annotation.Scheduled;

public class OutboxRelayScheduler {

    private final OutboxRelayService outboxRelayService;
    private final int batchSize;

    public OutboxRelayScheduler(OutboxRelayService outboxRelayService, int batchSize) {
        this.outboxRelayService = outboxRelayService;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${cfd.kafka.outbox.fixed-delay-ms:500}")
    public void flush() {
        outboxRelayService.flushPending(batchSize);
    }
}
