package com.cfd.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.common.kafka.outbox.OutboxRelayScheduler;
import com.cfd.common.kafka.outbox.OutboxRelayService;

@Configuration
public class OrderRelaySchedulerConfiguration {

    @Bean
    public OutboxRelayScheduler orderOutboxRelayScheduler(
            OutboxRelayService orderOutboxRelayService) {
        return new OutboxRelayScheduler(orderOutboxRelayService, 200);
    }
}
