package com.cfd.common.kafka.idempotent;

import java.util.function.Consumer;

/**
 * 幂等消费执行器。
 *
 * <p>封装了"先去重、再执行、后标记"的幂等消费模式，确保同一消息在同一消费组内只被处理一次。
 * 执行流程如下：
 * <ol>
 *     <li>调用 {@link ConsumerDedupStore#tryStartProcessing} 尝试获得处理权</li>
 *     <li>若获得处理权，执行传入的业务逻辑（{@link Consumer}）</li>
 *     <li>业务成功 → {@link ConsumerDedupStore#markProcessed}</li>
 *     <li>业务异常 → {@link ConsumerDedupStore#clearProcessing}，并重新抛出异常</li>
 * </ol>
 *
 * @author CFD Platform Team
 * @see ConsumerDedupStore
 */
public class IdempotentConsumerExecutor {

    private final ConsumerDedupStore dedupStore;

    /**
     * 创建幂等消费执行器。
     *
     * @param dedupStore 消费者去重存储
     */
    public IdempotentConsumerExecutor(ConsumerDedupStore dedupStore) {
        this.dedupStore = dedupStore;
    }

    /**
     * 以幂等方式执行消费逻辑。
     *
     * @param consumerGroup 消费组名称
     * @param messageId     消息唯一标识
     * @param payload       消息载荷
     * @param consumer      业务处理逻辑
     * @param <T>           载荷类型
     * @return 若消息被成功处理返回 {@code true}；若消息已被处理（重复）返回 {@code false}
     * @throws RuntimeException 业务逻辑抛出的运行时异常将被透传，同时清除去重标记以允许重试
     */
    public <T> boolean execute(String consumerGroup, String messageId, T payload, Consumer<T> consumer) {
        if (!dedupStore.tryStartProcessing(consumerGroup, messageId)) {
            return false;
        }
        try {
            consumer.accept(payload);
            dedupStore.markProcessed(consumerGroup, messageId);
            return true;
        } catch (RuntimeException ex) {
            dedupStore.clearProcessing(consumerGroup, messageId);
            throw ex;
        }
    }
}
