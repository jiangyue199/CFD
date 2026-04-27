package com.cfd.trading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.common.kafka.outbox.OutboxRelayScheduler;
import com.cfd.common.kafka.outbox.OutboxRelayService;

@Configuration
public class TradingRelaySchedulerConfiguration {

    @Bean
    public OutboxRelayScheduler tradingOutboxRelayScheduler(
            OutboxRelayService tradingOutboxRelayService) {
        return new OutboxRelayScheduler(tradingOutboxRelayService, 200);
    }
}
