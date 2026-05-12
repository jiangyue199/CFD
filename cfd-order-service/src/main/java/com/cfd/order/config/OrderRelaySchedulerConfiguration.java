package com.cfd.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.common.kafka.outbox.OutboxRelayScheduler;
import com.cfd.common.kafka.outbox.OutboxRelayService;

/**
 * Outbox 中继调度器配置类。
 *
 * <p>配置定时轮询 Outbox 表并将待发送消息投递至 Kafka 的调度器，轮询间隔为 200ms。</p>
 *
 * @author CFD Platform Team
 */
@Configuration
public class OrderRelaySchedulerConfiguration {

    /**
     * 创建 Outbox 中继调度器实例。
     *
     * @param orderOutboxRelayService Outbox 中继服务
     * @return Outbox 中继调度器
     */
    @Bean
    public OutboxRelayScheduler orderOutboxRelayScheduler(
            OutboxRelayService orderOutboxRelayService) {
        return new OutboxRelayScheduler(orderOutboxRelayService, 200);
    }
}
