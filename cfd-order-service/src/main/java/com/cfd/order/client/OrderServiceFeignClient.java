package com.cfd.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cfd.domain.model.OrderOpenRequest;
import com.cfd.domain.model.OrderResponse;

@FeignClient(name = "cfd-order-service", path = "/orders", url = "${services.order.url:http://localhost:8082}")
public interface OrderServiceFeignClient {

    @PostMapping("/open")
    OrderResponse open(@RequestBody OrderOpenRequest request);

    @GetMapping("/{orderId}")
    OrderResponse getById(@PathVariable("orderId") String orderId);
}
