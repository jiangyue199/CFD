package com.cfd.order.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cfd.common.kafka.idempotent.IdempotentConsumerExecutor;
import com.cfd.common.kafka.message.KafkaEnvelope;
import com.cfd.domain.model.TradeOpenedFeedback;
import com.cfd.order.service.OrderApplicationService;

@Component
public class OrderTradeFeedbackConsumer {

    private static final String GROUP_ID = "order-service-feedback";

    private final ObjectMapper objectMapper;
    private final IdempotentConsumerExecutor idempotentConsumerExecutor;
    private final OrderApplicationService orderApplicationService;

    public OrderTradeFeedbackConsumer(ObjectMapper objectMapper,
                                      IdempotentConsumerExecutor idempotentConsumerExecutor,
                                      OrderApplicationService orderApplicationService) {
        this.objectMapper = objectMapper;
        this.idempotentConsumerExecutor = idempotentConsumerExecutor;
        this.orderApplicationService = orderApplicationService;
    }

    @KafkaListener(topics = "${cfd.topic.tradeFeedback:cfd.trade.opened.feedback}", groupId = GROUP_ID)
    public void consume(String payload) throws Exception {
        JavaType envelopeType = objectMapper.getTypeFactory()
                .constructParametricType(KafkaEnvelope.class, TradeOpenedFeedback.class);
        KafkaEnvelope<TradeOpenedFeedback> envelope = objectMapper.readValue(payload, envelopeType);

        idempotentConsumerExecutor.execute(
                GROUP_ID,
                envelope.messageId(),
                envelope.payload(),
                orderApplicationService::onTradeOpened);
    }
}
