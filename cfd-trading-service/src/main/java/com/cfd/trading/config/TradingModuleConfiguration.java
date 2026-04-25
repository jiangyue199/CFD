package com.cfd.trading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.common.kafka.outbox.InMemoryOutboxRepository;
import com.cfd.common.kafka.outbox.OutboxRelayService;
import com.cfd.common.kafka.outbox.OutboxRepository;
import com.cfd.common.kafka.producer.ReliableKafkaPublisher;
import com.cfd.trading.domain.InMemoryOpenPositionRepository;
import com.cfd.trading.domain.OpenPositionRepository;

@Configuration
public class TradingModuleConfiguration {

    @Bean
    public OpenPositionRepository openPositionRepository() {
        return new InMemoryOpenPositionRepository();
    }

    @Bean
    public OutboxRepository tradingOutboxRepository() {
        return new InMemoryOutboxRepository();
    }

    @Bean
    public OutboxRelayService tradingOutboxRelayService(OutboxRepository tradingOutboxRepository,
                                                        ReliableKafkaPublisher reliableKafkaPublisher) {
        return new OutboxRelayService(tradingOutboxRepository, reliableKafkaPublisher);
    }
}
