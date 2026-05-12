package com.cfd.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.common.kafka.outbox.OutboxRelayService;
import com.cfd.common.kafka.outbox.persistence.MybatisPlusOutboxRepository;
import com.cfd.common.kafka.producer.ReliableKafkaPublisher;
import com.cfd.order.domain.OrderDomainService;

/**
 * 订单模块 Bean 配置类。
 *
 * <p>注册订单领域服务和 Outbox 中继服务的 Spring Bean。</p>
 *
 * @author CFD Platform Team
 */
@Configuration
public class OrderModuleConfiguration {

    /**
     * 创建订单领域服务实例。
     *
     * @return 订单领域服务
     */
    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainService();
    }

    /**
     * 创建 Outbox 中继服务实例，负责将 Outbox 消息投递至 Kafka。
     *
     * @param outboxRepository        Outbox 持久化仓储
     * @param reliableKafkaPublisher   可靠 Kafka 发布器
     * @return Outbox 中继服务
     */
    @Bean
    public OutboxRelayService orderOutboxRelayService(MybatisPlusOutboxRepository outboxRepository,
                                                      ReliableKafkaPublisher reliableKafkaPublisher) {
        return new OutboxRelayService(outboxRepository, reliableKafkaPublisher);
    }
}
