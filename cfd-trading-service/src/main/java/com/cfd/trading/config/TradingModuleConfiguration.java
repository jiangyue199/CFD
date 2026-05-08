package com.cfd.trading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.common.kafka.outbox.OutboxRelayService;
import com.cfd.common.kafka.outbox.persistence.MybatisPlusOutboxRepository;
import com.cfd.common.kafka.producer.ReliableKafkaPublisher;

@Configuration
public class TradingModuleConfiguration {

    @Bean
    public OutboxRelayService tradingOutboxRelayService(MybatisPlusOutboxRepository outboxRepository,
                                                        ReliableKafkaPublisher reliableKafkaPublisher) {
        return new OutboxRelayService(outboxRepository, reliableKafkaPublisher);
    }
}
