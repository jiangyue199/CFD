package com.cfd.risk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 风控服务启动类。
 *
 * <p>基于 Spring Boot 的微服务入口，启用服务发现、定时任务与 MyBatis-Plus Mapper 扫描。</p>
 *
 * @author CFD Platform Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@MapperScan("com.cfd.risk.persistence")
public class RiskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskServiceApplication.class, args);
    }
}
