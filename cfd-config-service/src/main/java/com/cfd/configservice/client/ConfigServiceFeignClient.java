package com.cfd.configservice.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cfd.configservice.api.ConfigUpdateRequest;

@FeignClient(
        name = "cfd-config-service",
        path = "/configs",
        url = "${services.config.url:http://localhost:8090}"
)
public interface ConfigServiceFeignClient {

    @GetMapping
    Map<String, String> all();

    @GetMapping("/{key}")
    String get(@PathVariable("key") String key);

    @PostMapping
    Map<String, String> update(@RequestBody ConfigUpdateRequest request);
}
