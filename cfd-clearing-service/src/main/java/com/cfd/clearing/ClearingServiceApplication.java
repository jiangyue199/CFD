package com.cfd.clearing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.cfd.clearing", "com.cfd.common.kafka"})
@EnableDiscoveryClient
@EnableScheduling
@MapperScan({"com.cfd.clearing.persistence", "com.cfd.common.kafka.outbox.persistence"})
public class ClearingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClearingServiceApplication.class, args);
    }
}
