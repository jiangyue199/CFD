package com.cfd.common.kafka.producer;

import java.util.concurrent.TimeUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

/**
 * 基于 Spring {@link KafkaTemplate} 的可靠消息发布器实现。
 *
 * <p>采用同步发送模式，调用 {@link KafkaTemplate#send} 后阻塞等待最多 5 秒，
 * 确保消息被 Kafka Broker 确认写入后方法才返回。若发送超时或失败，
 * 将包装为 {@link KafkaPublishException} 抛出。
 *
 * <p>配合 {@link com.cfd.common.kafka.config.KafkaReliabilityConfiguration} 中
 * 配置的 acks=all + 幂等生产者，提供最高级别的投递保障。
 *
 * @author CFD Platform Team
 * @see ReliableKafkaPublisher
 * @see KafkaPublishException
 */
public class SpringKafkaReliablePublisher implements ReliableKafkaPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 创建基于 KafkaTemplate 的可靠发布器。
     *
     * @param kafkaTemplate Spring Kafka 发送模板
     */
    public SpringKafkaReliablePublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String topic, String key, String payload) {
        try {
            SendResult<String, String> result = kafkaTemplate.send(topic, key, payload).get(5, TimeUnit.SECONDS);
            if (result.getRecordMetadata() == null) {
                throw new KafkaPublishException("Kafka send acknowledged without metadata", null);
            }
        } catch (Exception ex) {
            throw new KafkaPublishException("Failed to send message to topic " + topic, ex);
        }
    }
}
