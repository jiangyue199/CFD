package com.cfd.trading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cfd.common.kafka.outbox.OutboxRelayService;
import com.cfd.common.kafka.outbox.persistence.MybatisPlusOutboxRepository;
import com.cfd.common.kafka.producer.ReliableKafkaPublisher;

/**
 * 交易模块配置类。
 *
 * <p>负责注册交易服务所需的基础Bean，包括Outbox中继服务实例，
 * 用于保证Kafka消息的可靠投递。</p>
 *
 * @author CFD Platform Team
 */
@Configuration
public class TradingModuleConfiguration {

    /**
     * 创建交易服务的Outbox中继服务Bean。
     *
     * <p>该服务负责将Outbox表中的待发送消息中继到Kafka，确保消息可靠投递。</p>
     *
     * @param outboxRepository Outbox仓储
     * @param reliableKafkaPublisher 可靠Kafka发布者
     * @return Outbox中继服务实例
     */
    @Bean
    public OutboxRelayService tradingOutboxRelayService(MybatisPlusOutboxRepository outboxRepository,
                                                        ReliableKafkaPublisher reliableKafkaPublisher) {
        return new OutboxRelayService(outboxRepository, reliableKafkaPublisher);
    }
}
