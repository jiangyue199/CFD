package com.cfd.trading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.common.kafka.outbox.OutboxRelayService;
import com.cfd.common.kafka.outbox.RetryableOutboxRepository;
import com.cfd.common.kafka.producer.ReliableKafkaPublisher;
import com.cfd.trading.domain.OpenPositionRepository;
import com.cfd.trading.persistence.MybatisPlusOpenPositionRepository;

@Configuration
public class TradingModuleConfiguration {

    @Bean
    public OpenPositionRepository openPositionRepository(MybatisPlusOpenPositionRepository repository) {
        return repository;
    }

    @Bean
    public OutboxRelayService tradingOutboxRelayService(RetryableOutboxRepository tradingOutboxRepository,
                                                        ReliableKafkaPublisher reliableKafkaPublisher) {
        return new OutboxRelayService(tradingOutboxRepository, reliableKafkaPublisher);
    }
}
