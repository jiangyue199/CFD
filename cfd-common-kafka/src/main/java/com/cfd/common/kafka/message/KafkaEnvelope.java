package com.cfd.common.kafka.message;

import java.time.Instant;

/**
 * Kafka 通用消息信封。
 *
 * <p>对业务载荷进行标准化封装，附加元数据以支持消息追踪与幂等消费：
 * <ul>
 *     <li>{@code messageId} — 消息全局唯一标识，用于幂等去重</li>
 *     <li>{@code eventType} — 事件类型（如 {@code ORDER_CREATED}），便于消费端路由分发</li>
 *     <li>{@code sourceService} — 消息来源服务名称，用于跨服务追踪</li>
 *     <li>{@code occurredAt} — 事件发生时间</li>
 *     <li>{@code businessKey} — 业务关联键（如订单号），用于 Kafka 分区路由和日志关联</li>
 *     <li>{@code payload} — 实际业务载荷</li>
 * </ul>
 *
 * @param messageId     消息全局唯一标识
 * @param eventType     事件类型
 * @param sourceService 来源服务名称
 * @param occurredAt    事件发生时间
 * @param businessKey   业务关联键
 * @param payload       业务载荷
 * @param <T>           载荷类型
 * @author CFD Platform Team
 */
public record KafkaEnvelope<T>(
        String messageId,
        String eventType,
        String sourceService,
        Instant occurredAt,
        String businessKey,
        T payload
) {
}
