package com.cfd.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户服务启动类。
 *
 * <p>负责启动 CFD 用户微服务，提供用户注册、资料管理和 KYC 认证等功能。</p>
 *
 * @author CFD Platform Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.cfd.user.persistence")
public class UserServiceApplication {

    /**
     * 应用程序主入口。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
