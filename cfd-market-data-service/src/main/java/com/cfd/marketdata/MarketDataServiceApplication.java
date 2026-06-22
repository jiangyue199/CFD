package com.cfd.marketdata;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 行情服务启动类。
 *
 * <p>负责启动 CFD 行情微服务，提供市场报价数据的管理和查询功能。</p>
 *
 * @author CFD Platform Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.cfd.marketdata.persistence")
public class MarketDataServiceApplication {

    /**
     * 应用程序主入口。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(MarketDataServiceApplication.class, args);
    }
}
