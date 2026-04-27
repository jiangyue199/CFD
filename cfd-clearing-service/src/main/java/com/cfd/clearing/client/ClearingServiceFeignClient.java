package com.cfd.clearing.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cfd.domain.model.AccountBalanceResponse;

@FeignClient(
        name = "cfd-clearing-service",
        path = "/accounts",
        url = "${services.clearing.url:http://localhost:8084}"
)
public interface ClearingServiceFeignClient {

    @GetMapping("/{userId}/balance")
    AccountBalanceResponse getBalance(@PathVariable("userId") String userId);
}
