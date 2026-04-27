package com.cfd.trading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.cfd.trading", "com.cfd.common.kafka"})
@EnableDiscoveryClient
@EnableScheduling
@MapperScan({"com.cfd.trading.persistence", "com.cfd.common.kafka.outbox.persistence"})
public class TradingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingServiceApplication.class, args);
    }
}
