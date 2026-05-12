package com.cfd.common.kafka.outbox;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * 发件箱定时轮询调度器。
 *
 * <p>基于 Spring {@link Scheduled} 定时任务，按固定延迟（默认 500ms，可通过
 * {@code cfd.kafka.outbox.fixed-delay-ms} 配置）周期性调用
 * {@link OutboxRelayService#flushPending(int)} 将待发布消息投递到 Kafka。
 *
 * @author CFD Platform Team
 * @see OutboxRelayService
 */
public class OutboxRelayScheduler {

    private final OutboxRelayService outboxRelayService;
    private final int batchSize;

    /**
     * 创建发件箱调度器。
     *
     * @param outboxRelayService 发件箱中继服务
     * @param batchSize          每次轮询处理的最大消息数
     */
    public OutboxRelayScheduler(OutboxRelayService outboxRelayService, int batchSize) {
        this.outboxRelayService = outboxRelayService;
        this.batchSize = batchSize;
    }

    /**
     * 定时刷新任务入口。
     *
     * <p>由 Spring 调度框架按 {@code cfd.kafka.outbox.fixed-delay-ms} 配置的间隔周期调用。
     */
    @Scheduled(fixedDelayString = "${cfd.kafka.outbox.fixed-delay-ms:500}")
    public void flush() {
        outboxRelayService.flushPending(batchSize);
    }
}
