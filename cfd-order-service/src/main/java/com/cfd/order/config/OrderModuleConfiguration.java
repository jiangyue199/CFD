package com.cfd.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.common.kafka.outbox.OutboxRelayService;
import com.cfd.common.kafka.outbox.persistence.MybatisPlusOutboxRepository;
import com.cfd.common.kafka.producer.ReliableKafkaPublisher;
import com.cfd.order.domain.OrderDomainService;

@Configuration
public class OrderModuleConfiguration {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainService();
    }

    @Bean
    public OutboxRelayService orderOutboxRelayService(MybatisPlusOutboxRepository outboxRepository,
                                                      ReliableKafkaPublisher reliableKafkaPublisher) {
        return new OutboxRelayService(outboxRepository, reliableKafkaPublisher);
    }
}
