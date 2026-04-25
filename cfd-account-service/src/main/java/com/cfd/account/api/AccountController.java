package com.cfd.account.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.account.domain.AccountCurrencyBalance;
import com.cfd.account.service.AccountApplicationService;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountApplicationService accountApplicationService;

    public AccountController(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @PostMapping("/deposit")
    public AccountBalanceResponse deposit(@Validated @RequestBody AccountOperationRequest request) {
        return toResponse(accountApplicationService.deposit(request.userId(), request.currency(), request.amount()));
    }

    @PostMapping("/withdraw")
    public AccountBalanceResponse withdraw(@Validated @RequestBody AccountOperationRequest request) {
        return toResponse(accountApplicationService.withdraw(request.userId(), request.currency(), request.amount()));
    }

    @GetMapping("/{userId}/{currency}")
    public AccountBalanceResponse query(@PathVariable String userId, @PathVariable String currency) {
        return toResponse(accountApplicationService.query(userId, currency));
    }

    private AccountBalanceResponse toResponse(AccountCurrencyBalance balance) {
        return new AccountBalanceResponse(balance.getUserId(), balance.getCurrency(), balance.getAvailable());
    }
}
