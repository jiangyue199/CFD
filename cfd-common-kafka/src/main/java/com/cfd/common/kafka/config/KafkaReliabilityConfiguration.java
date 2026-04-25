package com.cfd.common.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.cfd.common.kafka.idempotent.ConsumerDedupStore;
import com.cfd.common.kafka.idempotent.IdempotentConsumerExecutor;
import com.cfd.common.kafka.idempotent.InMemoryConsumerDedupStore;
import com.cfd.common.kafka.producer.ReliableKafkaPublisher;
import com.cfd.common.kafka.producer.SpringKafkaReliablePublisher;

@Configuration
public class KafkaReliabilityConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ConsumerDedupStore consumerDedupStore() {
        return new InMemoryConsumerDedupStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentConsumerExecutor idempotentConsumerExecutor(ConsumerDedupStore dedupStore) {
        return new IdempotentConsumerExecutor(dedupStore);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> properties = new HashMap<>();
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

    @Bean
    @ConditionalOnMissingBean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReliableKafkaPublisher reliableKafkaPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        return new SpringKafkaReliablePublisher(kafkaTemplate);
    }
}
