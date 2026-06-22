package com.cfd.order.messaging;

import com.cfd.common.kafka.idempotent.IdempotentConsumerExecutor;
import com.cfd.common.kafka.message.KafkaEnvelope;
import com.cfd.domain.model.TradeOpenedFeedback;
import com.cfd.order.service.OrderApplicationService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 交易反馈 Kafka 消费者。
 *
 * <p>监听交易开仓反馈主题，接收交易系统的开仓确认消息，
 * 通过幂等消费器保证消息处理的幂等性，并更新订单状态为已开仓。</p>
 *
 * @author CFD Platform Team
 */
@Component
public class OrderTradeFeedbackConsumer {

    /** 消费者组ID */
    private static final String GROUP_ID = "order-service-feedback";

    private final ObjectMapper objectMapper;
    private final IdempotentConsumerExecutor idempotentConsumerExecutor;
    private final OrderApplicationService orderApplicationService;

    /**
     * 构造交易反馈消费者。
     *
     * @param objectMapper               JSON 序列化工具
     * @param idempotentConsumerExecutor  幂等消费执行器
     * @param orderApplicationService    订单应用服务
     */
    public OrderTradeFeedbackConsumer(ObjectMapper objectMapper,
                                      IdempotentConsumerExecutor idempotentConsumerExecutor,
                                      OrderApplicationService orderApplicationService) {
        this.objectMapper = objectMapper;
        this.idempotentConsumerExecutor = idempotentConsumerExecutor;
        this.orderApplicationService = orderApplicationService;
    }

    /**
     * 消费交易开仓反馈消息。
     *
     * <p>解析 Kafka 消息体中的信封格式数据，通过幂等执行器处理开仓确认。</p>
     *
     * @param payload Kafka 消息原始 JSON 字符串
     * @throws Exception 反序列化或处理异常
     */
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
