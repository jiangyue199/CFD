package com.cfd.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cfd.domain.model.RiskCheckRequest;
import com.cfd.domain.model.RiskCheckResponse;

@FeignClient(name = "cfd-risk-service", path = "/risk", url = "${services.risk.url:http://localhost:8081}")
public interface RiskFeignClient {

    @PostMapping("/open/check")
    RiskCheckResponse checkOpenRisk(@RequestBody RiskCheckRequest request);
}
