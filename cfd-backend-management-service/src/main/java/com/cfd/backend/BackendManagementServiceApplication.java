package com.cfd.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 后台管理服务启动类。
 *
 * <p>负责启动 CFD 后台管理微服务，提供管理仪表盘及运营指标查询功能。</p>
 *
 * @author CFD Platform Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.cfd.backend.persistence")
public class BackendManagementServiceApplication {

    /**
     * 应用程序主入口。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendManagementServiceApplication.class, args);
    }
}
