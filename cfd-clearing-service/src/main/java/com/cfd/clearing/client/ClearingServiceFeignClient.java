package com.cfd.clearing.client;

import com.cfd.domain.model.AccountBalanceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 清算服务Feign客户端接口。
 *
 * <p>供其他微服务通过Feign远程调用清算服务的账户查询接口。
 * 默认连接地址为 http://localhost:8084。</p>
 *
 * @author CFD Platform Team
 */
@FeignClient(
        name = "cfd-clearing-service",
        path = "/accounts",
        url = "${services.clearing.url:http://localhost:8084}"
)
public interface ClearingServiceFeignClient {

    /**
     * 获取指定用户的账户余额。
     *
     * @param userId 用户ID
     * @return 账户余额响应
     */
    @GetMapping("/{userId}/balance")
    AccountBalanceResponse getBalance(@PathVariable("userId") String userId);
}
