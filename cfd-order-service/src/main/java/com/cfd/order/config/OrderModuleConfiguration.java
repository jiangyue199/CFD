package com.cfd.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.common.kafka.outbox.OutboxRelayService;
import com.cfd.common.kafka.outbox.RetryableOutboxRepository;
import com.cfd.common.kafka.outbox.persistence.MybatisPlusOutboxRepository;
import com.cfd.common.kafka.producer.ReliableKafkaPublisher;
import com.cfd.order.domain.OrderDomainService;
import com.cfd.order.domain.OrderRepository;
import com.cfd.order.persistence.MybatisPlusOrderRepository;

@Configuration
public class OrderModuleConfiguration {

    @Bean
    public OrderRepository orderRepository(MybatisPlusOrderRepository repository) {
        return repository;
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainService();
    }

    @Bean
    public RetryableOutboxRepository orderOutboxRepository(MybatisPlusOutboxRepository repository) {
        return repository;
    }

    @Bean
    public OutboxRelayService orderOutboxRelayService(RetryableOutboxRepository orderOutboxRepository,
                                                      ReliableKafkaPublisher reliableKafkaPublisher) {
        return new OutboxRelayService(orderOutboxRepository, reliableKafkaPublisher);
    }
}
