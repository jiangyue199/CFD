package com.cfd.trading.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cfd.domain.model.OpenPositionResponse;

@FeignClient(name = "cfd-trading-service",
        path = "/positions",
        url = "${services.trading.url:http://localhost:8083}")
public interface TradingServiceFeignClient {

    @GetMapping("/{orderId}")
    OpenPositionResponse getByOrderId(@PathVariable("orderId") String orderId);

    @GetMapping("/user/{userId}")
    List<OpenPositionResponse> listByUser(@PathVariable("userId") String userId);
}
