package com.cfd.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.common.kafka.outbox.InMemoryOutboxRepository;
import com.cfd.common.kafka.outbox.OutboxRelayService;
import com.cfd.common.kafka.outbox.OutboxRepository;
import com.cfd.common.kafka.producer.ReliableKafkaPublisher;
import com.cfd.order.domain.InMemoryOrderRepository;
import com.cfd.order.domain.OrderDomainService;
import com.cfd.order.domain.OrderRepository;

@Configuration
public class OrderModuleConfiguration {

    @Bean
    public OrderRepository orderRepository() {
        return new InMemoryOrderRepository();
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainService();
    }

    @Bean
    public OutboxRepository orderOutboxRepository() {
        return new InMemoryOutboxRepository();
    }

    @Bean
    public OutboxRelayService orderOutboxRelayService(OutboxRepository orderOutboxRepository,
                                                      ReliableKafkaPublisher reliableKafkaPublisher) {
        return new OutboxRelayService(orderOutboxRepository, reliableKafkaPublisher);
    }
}
