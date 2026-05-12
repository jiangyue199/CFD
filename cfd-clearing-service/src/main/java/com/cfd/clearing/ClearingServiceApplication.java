package com.cfd.clearing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * CFD清算服务启动类。
 *
 * <p>作为清算微服务的入口，启用服务发现、定时任务调度以及MyBatis-Plus Mapper扫描。
 * 负责消费交易开仓事件并执行账户资金清算。</p>
 *
 * @author CFD Platform Team
 */
@SpringBootApplication(scanBasePackages = {"com.cfd.clearing", "com.cfd.common.kafka"})
@EnableDiscoveryClient
@EnableScheduling
@MapperScan({"com.cfd.clearing.persistence", "com.cfd.common.kafka.outbox.persistence"})
public class ClearingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClearingServiceApplication.class, args);
    }
}
