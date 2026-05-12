package com.cfd.common.kafka.outbox;

/**
 * 发件箱消息投递状态枚举。
 *
 * <ul>
 *     <li>{@link #PENDING} — 待发布，消息已写入发件箱但尚未投递到 Kafka</li>
 *     <li>{@link #PUBLISHED} — 已发布，消息已成功投递到 Kafka</li>
 *     <li>{@link #FAILED} — 发布失败，投递过程中发生异常</li>
 * </ul>
 *
 * @author CFD Platform Team
 * @see OutboxMessage
 */
public enum OutboxStatus {
    /** 待发布 */
    PENDING,
    /** 已发布 */
    PUBLISHED,
    /** 发布失败 */
    FAILED
}
