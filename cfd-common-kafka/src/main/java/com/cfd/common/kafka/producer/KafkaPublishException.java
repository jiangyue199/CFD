package com.cfd.common.kafka.producer;

/**
 * Kafka 消息发布异常。
 *
 * <p>当 {@link ReliableKafkaPublisher} 发布消息失败时抛出此运行时异常，
 * 封装了发送超时、网络错误、Broker 拒绝等底层异常信息。
 *
 * @author CFD Platform Team
 * @see ReliableKafkaPublisher
 */
public class KafkaPublishException extends RuntimeException {

    /**
     * 创建 Kafka 发布异常。
     *
     * @param message 异常描述信息
     * @param cause   原始异常（可为 {@code null}）
     */
    public KafkaPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
