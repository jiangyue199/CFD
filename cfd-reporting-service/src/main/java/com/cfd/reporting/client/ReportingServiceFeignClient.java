package com.cfd.reporting.client;

import com.cfd.reporting.service.ReportingService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 报表服务 Feign 客户端接口。
 *
 * <p>供其他微服务通过 Feign 远程调用报表服务的日报生成接口。</p>
 *
 * @author CFD Platform Team
 */
@FeignClient(
        name = "cfd-reporting-service",
        path = "/reports",
        url = "${services.reporting.url:http://localhost:8089}")
public interface ReportingServiceFeignClient {

    /**
     * 远程调用日报生成接口。
     *
     * @param businessDate 营业日期
     * @return 日报信息
     */
    @GetMapping("/daily/{businessDate}")
    ReportingService.DailyReport generateDaily(@PathVariable("businessDate") String businessDate);
}
