package com.cfd.trading.config;

import com.cfd.common.kafka.outbox.OutboxRelayScheduler;
import com.cfd.common.kafka.outbox.OutboxRelayService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 交易服务Outbox中继调度器配置类。
 *
 * <p>配置定时调度器，定期轮询Outbox表并将待发送消息中继到Kafka。
 * 调度间隔为200毫秒。</p>
 *
 * @author CFD Platform Team
 */
@Configuration
public class TradingRelaySchedulerConfiguration {

    /**
     * 创建交易服务的Outbox中继调度器Bean。
     *
     * @param tradingOutboxRelayService 交易Outbox中继服务
     * @return Outbox中继调度器实例，轮询间隔200毫秒
     */
    @Bean
    public OutboxRelayScheduler tradingOutboxRelayScheduler(
            OutboxRelayService tradingOutboxRelayService) {
        return new OutboxRelayScheduler(tradingOutboxRelayService, 200);
    }
}
