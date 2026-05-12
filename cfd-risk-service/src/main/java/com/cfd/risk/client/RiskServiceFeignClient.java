package com.cfd.risk.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cfd.domain.model.RiskCheckRequest;
import com.cfd.domain.model.RiskCheckResponse;

/**
 * 风控服务 Feign 客户端接口。
 *
 * <p>供其他微服务通过 Feign 远程调用风控服务的开仓校验接口。</p>
 *
 * @author CFD Platform Team
 */
@FeignClient(
        name = "cfd-risk-service",
        path = "/risk",
        url = "${services.risk.url:http://localhost:8081}"
)
public interface RiskServiceFeignClient {

    /**
     * 调用风控服务执行开仓风控校验。
     *
     * @param request 风控校验请求
     * @return 风控校验结果
     */
    @PostMapping("/open/check")
    RiskCheckResponse checkOpenRisk(@RequestBody RiskCheckRequest request);
}
