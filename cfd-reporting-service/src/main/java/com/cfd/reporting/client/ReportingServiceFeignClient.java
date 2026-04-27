package com.cfd.reporting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cfd.reporting.service.ReportingService;

@FeignClient(
        name = "cfd-reporting-service",
        path = "/reports",
        url = "${services.reporting.url:http://localhost:8089}")
public interface ReportingServiceFeignClient {

    @GetMapping("/daily/{businessDate}")
    ReportingService.DailyReport generateDaily(@PathVariable("businessDate") String businessDate);
}
