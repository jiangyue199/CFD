package com.cfd.clearing.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cfd.clearing.service.ClearingApplicationService;
import com.cfd.common.kafka.idempotent.IdempotentConsumerExecutor;
import com.cfd.common.kafka.message.KafkaEnvelope;
import com.cfd.domain.model.TradeOpenedEvent;

/**
 * 交易开仓事件Kafka消费者。
 *
 * <p>监听Kafka中的{@code TradeOpenedEvent}消息，反序列化后通过幂等消费执行器
 * 调用{@link ClearingApplicationService#handleTradeOpened}执行清算逻辑，
 * 确保消息不被重复消费。</p>
 *
 * @author CFD Platform Team
 */
@Component
public class TradeOpenedConsumer {

    private static final String GROUP_ID = "clearing-trade-opened";

    private final ObjectMapper objectMapper;
    private final IdempotentConsumerExecutor idempotentConsumerExecutor;
    private final ClearingApplicationService clearingApplicationService;

    public TradeOpenedConsumer(ObjectMapper objectMapper,
                               IdempotentConsumerExecutor idempotentConsumerExecutor,
                               ClearingApplicationService clearingApplicationService) {
        this.objectMapper = objectMapper;
        this.idempotentConsumerExecutor = idempotentConsumerExecutor;
        this.clearingApplicationService = clearingApplicationService;
    }

    /**
     * 消费交易开仓事件消息。
     *
     * <p>从Kafka接收JSON格式的{@link KafkaEnvelope}消息，解析出{@link TradeOpenedEvent}
     * 并通过幂等执行器处理清算逻辑。</p>
     *
     * @param payload Kafka消息原始JSON字符串
     * @throws Exception 消息反序列化或处理异常
     */
    @KafkaListener(topics = "${cfd.topic.tradeOpened:cfd.trade.opened.event}", groupId = GROUP_ID)
    public void consume(String payload) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(KafkaEnvelope.class, TradeOpenedEvent.class);
        KafkaEnvelope<TradeOpenedEvent> envelope = objectMapper.readValue(payload, type);

        idempotentConsumerExecutor.execute(
                GROUP_ID,
                envelope.messageId(),
                envelope.payload(),
                clearingApplicationService::handleTradeOpened);
    }
}
