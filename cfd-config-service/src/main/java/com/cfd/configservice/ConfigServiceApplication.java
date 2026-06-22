package com.cfd.configservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 配置服务启动类。
 *
 * <p>负责启动 CFD 配置微服务，提供运行时配置的键值对管理功能。</p>
 *
 * @author CFD Platform Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.cfd.configservice.persistence")
public class ConfigServiceApplication {

    /**
     * 应用程序主入口。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}
