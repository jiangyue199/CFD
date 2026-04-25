package com.cfd.clearing.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.clearing.service.ClearingApplicationService;
import com.cfd.domain.model.AccountBalanceResponse;

@RestController
@RequestMapping("/accounts")
public class ClearingQueryController {

    private final ClearingApplicationService clearingApplicationService;

    public ClearingQueryController(ClearingApplicationService clearingApplicationService) {
        this.clearingApplicationService = clearingApplicationService;
    }

    @GetMapping("/{userId}/balance")
    public AccountBalanceResponse getBalance(@PathVariable String userId) {
        return clearingApplicationService.queryAccount(userId);
    }
}
