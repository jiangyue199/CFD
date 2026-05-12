package com.cfd.backend.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 后台管理服务 Feign 客户端接口。
 *
 * <p>供其他微服务通过 Feign 远程调用后台管理服务的仪表盘接口。</p>
 *
 * @author CFD Platform Team
 */
@FeignClient(
        name = "cfd-backend-management-service",
        path = "/backend",
        url = "${services.backendManagement.url:http://localhost:8091}"
)
public interface BackendManagementServiceFeignClient {

    /**
     * 远程获取仪表盘运营指标快照。
     *
     * @return 包含各项运营指标的键值对映射
     */
    @GetMapping("/dashboard")
    Map<String, Object> dashboard();
}
