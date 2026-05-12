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

/**
 * 账户 REST 控制器。
 *
 * <p>提供入金、出金和余额查询的 HTTP 接口，
 * 所有请求路径以 {@code /accounts} 为前缀。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountApplicationService accountApplicationService;

    /**
     * 构造账户控制器。
     *
     * @param accountApplicationService 账户应用服务
     */
    public AccountController(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    /**
     * 入金操作。
     *
     * @param request 包含用户ID、币种和金额的入金请求
     * @return 操作后的账户余额响应
     */
    @PostMapping("/deposit")
    public AccountBalanceResponse deposit(@Validated @RequestBody AccountOperationRequest request) {
        return toResponse(accountApplicationService.deposit(request.userId(), request.currency(), request.amount()));
    }

    /**
     * 出金操作。
     *
     * @param request 包含用户ID、币种和金额的出金请求
     * @return 操作后的账户余额响应
     */
    @PostMapping("/withdraw")
    public AccountBalanceResponse withdraw(@Validated @RequestBody AccountOperationRequest request) {
        return toResponse(accountApplicationService.withdraw(request.userId(), request.currency(), request.amount()));
    }

    /**
     * 查询指定用户和币种的账户余额。
     *
     * @param userId   用户ID
     * @param currency 币种代码
     * @return 当前账户余额响应
     */
    @GetMapping("/{userId}/{currency}")
    public AccountBalanceResponse query(@PathVariable String userId, @PathVariable String currency) {
        return toResponse(accountApplicationService.query(userId, currency));
    }

    /**
     * 将领域模型转换为 API 响应对象。
     *
     * @param balance 账户币种余额领域对象
     * @return 账户余额响应
     */
    private AccountBalanceResponse toResponse(AccountCurrencyBalance balance) {
        return new AccountBalanceResponse(balance.getUserId(), balance.getCurrency(), balance.getAvailable());
    }
}
