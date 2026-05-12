package com.cfd.trading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * CFD交易服务启动类。
 *
 * <p>作为交易微服务的入口，启用服务发现、定时任务调度以及MyBatis-Plus Mapper扫描。
 * 负责消费开仓指令并管理持仓生命周期。</p>
 *
 * @author CFD Platform Team
 */
@SpringBootApplication(scanBasePackages = {"com.cfd.trading", "com.cfd.common.kafka"})
@EnableDiscoveryClient
@EnableScheduling
@MapperScan({"com.cfd.trading.persistence", "com.cfd.common.kafka.outbox.persistence"})
public class TradingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingServiceApplication.class, args);
    }
}
