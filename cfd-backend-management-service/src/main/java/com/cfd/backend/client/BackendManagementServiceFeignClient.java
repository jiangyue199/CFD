package com.cfd.backend.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "cfd-backend-management-service",
        path = "/backend",
        url = "${services.backendManagement.url:http://localhost:8091}"
)
public interface BackendManagementServiceFeignClient {

    @GetMapping("/dashboard")
    Map<String, Object> dashboard();
}
