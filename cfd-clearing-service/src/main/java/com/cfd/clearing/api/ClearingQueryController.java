package com.cfd.clearing.api;

import com.cfd.clearing.service.ClearingApplicationService;
import com.cfd.domain.model.AccountBalanceResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 清算账户查询控制器。
 *
 * <p>提供REST接口用于查询用户账户余额信息，包括可用余额和冻结保证金。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/accounts")
public class ClearingQueryController {

    private final ClearingApplicationService clearingApplicationService;

    public ClearingQueryController(ClearingApplicationService clearingApplicationService) {
        this.clearingApplicationService = clearingApplicationService;
    }

    /**
     * 查询指定用户的账户余额。
     *
     * @param userId 用户ID
     * @return 账户余额响应，包含可用余额和冻结保证金
     */
    @GetMapping("/{userId}/balance")
    public AccountBalanceResponse getBalance(@PathVariable String userId) {
        return clearingApplicationService.queryAccount(userId);
    }
}
