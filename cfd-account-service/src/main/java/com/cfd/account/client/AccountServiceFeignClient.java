package com.cfd.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cfd.account.api.AccountBalanceResponse;
import com.cfd.account.api.AccountOperationRequest;

/**
 * 账户服务 Feign 客户端接口。
 *
 * <p>供其他微服务通过 Feign 远程调用账户服务的入金、出金和余额查询接口。</p>
 *
 * @author CFD Platform Team
 */
@FeignClient(name = "cfd-account-service", path = "/accounts", url = "${services.account.url:http://localhost:8086}")
public interface AccountServiceFeignClient {

    /**
     * 远程调用入金接口。
     *
     * @param request 入金请求
     * @return 操作后的账户余额
     */
    @PostMapping("/deposit")
    AccountBalanceResponse deposit(@RequestBody AccountOperationRequest request);

    /**
     * 远程调用出金接口。
     *
     * @param request 出金请求
     * @return 操作后的账户余额
     */
    @PostMapping("/withdraw")
    AccountBalanceResponse withdraw(@RequestBody AccountOperationRequest request);

    /**
     * 远程查询指定用户和币种的账户余额。
     *
     * @param userId   用户ID
     * @param currency 币种代码
     * @return 当前账户余额
     */
    @GetMapping("/{userId}/{currency}")
    AccountBalanceResponse query(@PathVariable("userId") String userId, @PathVariable("currency") String currency);
}
