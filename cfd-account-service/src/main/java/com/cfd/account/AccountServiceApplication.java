package com.cfd.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 账户服务启动类。
 *
 * <p>负责启动 CFD 账户微服务，管理用户账户的币种余额，
 * 支持入金、出金及余额查询等操作。</p>
 *
 * @author CFD Platform Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.cfd.account.persistence")
public class AccountServiceApplication {

    /**
     * 应用程序主入口。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
