package com.cfd.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.cfd.backend.persistence")
public class BackendManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendManagementServiceApplication.class, args);
    }
}
