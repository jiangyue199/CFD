package com.cfd.trading.messaging;

import com.cfd.common.kafka.idempotent.IdempotentConsumerExecutor;
import com.cfd.common.kafka.message.KafkaEnvelope;
import com.cfd.domain.model.OrderOpenCommand;
import com.cfd.trading.service.TradingApplicationService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 开仓指令Kafka消费者。
 *
 * <p>监听Kafka中的{@code OrderOpenCommand}消息，反序列化后通过幂等消费执行器
 * 调用{@link TradingApplicationService#handleOrderOpen}处理开仓逻辑，
 * 确保消息不被重复消费。</p>
 *
 * @author CFD Platform Team
 */
@Component
public class OrderOpenConsumer {

    private static final String GROUP_ID = "trading-order-open";

    private final ObjectMapper objectMapper;
    private final IdempotentConsumerExecutor idempotentConsumerExecutor;
    private final TradingApplicationService tradingApplicationService;

    public OrderOpenConsumer(ObjectMapper objectMapper,
                             IdempotentConsumerExecutor idempotentConsumerExecutor,
                             TradingApplicationService tradingApplicationService) {
        this.objectMapper = objectMapper;
        this.idempotentConsumerExecutor = idempotentConsumerExecutor;
        this.tradingApplicationService = tradingApplicationService;
    }

    /**
     * 消费开仓指令消息。
     *
     * <p>从Kafka接收JSON格式的{@link KafkaEnvelope}消息，解析出{@link OrderOpenCommand}
     * 并通过幂等执行器处理。</p>
     *
     * @param payload Kafka消息原始JSON字符串
     * @throws Exception 消息反序列化或处理异常
     */
    @KafkaListener(topics = "${cfd.topic.orderOpen:cfd.order.open.command}", groupId = GROUP_ID)
    public void consume(String payload) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(KafkaEnvelope.class, OrderOpenCommand.class);
        KafkaEnvelope<OrderOpenCommand> envelope = objectMapper.readValue(payload, type);

        idempotentConsumerExecutor.execute(
                GROUP_ID,
                envelope.messageId(),
                envelope.payload(),
                tradingApplicationService::handleOrderOpen);
    }
}
