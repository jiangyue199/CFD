package com.cfd.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cfd.account.api.AccountBalanceResponse;
import com.cfd.account.api.AccountOperationRequest;

@FeignClient(name = "cfd-account-service", path = "/accounts", url = "${services.account.url:http://localhost:8086}")
public interface AccountServiceFeignClient {

    @PostMapping("/deposit")
    AccountBalanceResponse deposit(@RequestBody AccountOperationRequest request);

    @PostMapping("/withdraw")
    AccountBalanceResponse withdraw(@RequestBody AccountOperationRequest request);

    @GetMapping("/{userId}/{currency}")
    AccountBalanceResponse query(@PathVariable("userId") String userId, @PathVariable("currency") String currency);
}
