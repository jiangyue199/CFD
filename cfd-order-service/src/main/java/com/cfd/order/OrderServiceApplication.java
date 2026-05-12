package com.cfd.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 订单服务启动类。
 *
 * <p>基于 Spring Boot 的微服务入口，启用服务发现、Feign 客户端、定时任务调度
 * 以及 MyBatis-Plus Mapper 扫描。</p>
 *
 * @author CFD Platform Team
 */
@SpringBootApplication(scanBasePackages = {"com.cfd.order", "com.cfd.common.kafka"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@MapperScan({"com.cfd.order.persistence", "com.cfd.common.kafka.outbox.persistence"})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
