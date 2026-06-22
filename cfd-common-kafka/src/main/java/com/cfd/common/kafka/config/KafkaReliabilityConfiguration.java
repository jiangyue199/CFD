package com.cfd.common.kafka.config;

import com.cfd.common.kafka.idempotent.ConsumerDedupStore;
import com.cfd.common.kafka.idempotent.IdempotentConsumerExecutor;
import com.cfd.common.kafka.idempotent.InMemoryAndExpiryConsumerDedupStore;
import com.cfd.common.kafka.producer.ReliableKafkaPublisher;
import com.cfd.common.kafka.producer.SpringKafkaReliablePublisher;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 可靠性自动配置类。
 *
 * <p>作为 Spring 自动配置入口，统一注册 Kafka 可靠发布与幂等消费所需的核心 Bean：
 * <ul>
 *     <li>{@link ConsumerDedupStore} — 消费者去重存储（默认基于内存 + TTL 过期策略）</li>
 *     <li>{@link IdempotentConsumerExecutor} — 幂等消费执行器</li>
 *     <li>{@link org.springframework.kafka.core.ProducerFactory} — 生产者工厂（acks=all、开启幂等性）</li>
 *     <li>{@link org.springframework.kafka.core.KafkaTemplate} — Kafka 发送模板</li>
 *     <li>{@link ReliableKafkaPublisher} — 可靠消息发布器</li>
 * </ul>
 *
 * <p>所有 Bean 均标注 {@link ConditionalOnMissingBean}，应用可自行覆盖任一实现。
 *
 * @author CFD Platform Team
 * @see ReliableKafkaPublisher
 * @see IdempotentConsumerExecutor
 */
@Configuration
public class KafkaReliabilityConfiguration {

    /**
     * 注册默认的消费者去重存储。
     *
     * <p>使用 {@link InMemoryAndExpiryConsumerDedupStore}，TTL 默认 24 小时。
     *
     * @return 消费者去重存储实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ConsumerDedupStore consumerDedupStore() {
        return new InMemoryAndExpiryConsumerDedupStore(Duration.ofHours(24));
    }

    /**
     * 注册幂等消费执行器。
     *
     * @param dedupStore 消费者去重存储
     * @return 幂等消费执行器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentConsumerExecutor idempotentConsumerExecutor(ConsumerDedupStore dedupStore) {
        return new IdempotentConsumerExecutor(dedupStore);
    }

    /**
     * 注册 Kafka 生产者工厂。
     *
     * <p>关键配置：
     * <ul>
     *     <li>{@code acks=all} — 所有副本确认后才算写入成功</li>
     *     <li>{@code enable.idempotence=true} — 开启生产者幂等性，防止网络重试导致重复</li>
     *     <li>{@code retries=Integer.MAX_VALUE} — 配合幂等性，持续重试直到成功或超时</li>
     *     <li>{@code max.in.flight.requests.per.connection=5} — 幂等性允许的最大并行请求数</li>
     * </ul>
     *
     * @param bootstrapServers Kafka 集群地址，默认 {@code localhost:9092}
     * @return 生产者工厂实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ProducerFactory<String, String> producerFactory(
            @org.springframework.beans.factory.annotation.Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    /**
     * 注册 KafkaTemplate。
     *
     * @param producerFactory 生产者工厂
     * @return KafkaTemplate 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * 注册可靠消息发布器。
     *
     * <p>默认使用 {@link SpringKafkaReliablePublisher}，基于 {@link KafkaTemplate} 同步发送。
     *
     * @param kafkaTemplate Kafka 发送模板
     * @return 可靠消息发布器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ReliableKafkaPublisher reliableKafkaPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        return new SpringKafkaReliablePublisher(kafkaTemplate);
    }
}
