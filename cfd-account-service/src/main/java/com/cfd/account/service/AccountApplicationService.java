package com.cfd.account.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.cfd.account.domain.AccountBalanceRepository;
import com.cfd.account.domain.AccountCurrencyBalance;

/**
 * 账户应用服务。
 *
 * <p>封装账户相关的业务逻辑，包括入金、出金和余额查询，
 * 作为控制器与领域层之间的中介。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class AccountApplicationService {

    private final AccountBalanceRepository accountBalanceRepository;

    /**
     * 构造账户应用服务。
     *
     * @param accountBalanceRepository 账户余额仓储
     */
    public AccountApplicationService(AccountBalanceRepository accountBalanceRepository) {
        this.accountBalanceRepository = accountBalanceRepository;
    }

    /**
     * 执行入金操作。
     *
     * <p>若该用户和币种的账户不存在，则先创建零余额账户，再执行入金。</p>
     *
     * @param userId   用户ID
     * @param currency 币种代码
     * @param amount   入金金额
     * @return 入金后的账户余额
     */
    public AccountCurrencyBalance deposit(String userId, String currency, BigDecimal amount) {
        AccountCurrencyBalance balance = accountBalanceRepository.find(userId, currency)
                .orElseGet(() -> accountBalanceRepository.save(new AccountCurrencyBalance(userId, currency, BigDecimal.ZERO)));
        balance.deposit(amount);
        accountBalanceRepository.save(balance);
        return balance;
    }

    /**
     * 执行出金操作。
     *
     * @param userId   用户ID
     * @param currency 币种代码
     * @param amount   出金金额
     * @return 出金后的账户余额
     * @throws IllegalArgumentException 当账户不存在时抛出
     * @throws IllegalStateException    当余额不足时抛出
     */
    public AccountCurrencyBalance withdraw(String userId, String currency, BigDecimal amount) {
        AccountCurrencyBalance balance = accountBalanceRepository.find(userId, currency)
                .orElseThrow(() -> new IllegalArgumentException("Account balance not found"));
        balance.withdraw(amount);
        accountBalanceRepository.save(balance);
        return balance;
    }

    /**
     * 查询账户余额。
     *
     * <p>若该用户和币种的账户不存在，则自动创建零余额账户并返回。</p>
     *
     * @param userId   用户ID
     * @param currency 币种代码
     * @return 当前账户余额
     */
    public AccountCurrencyBalance query(String userId, String currency) {
        return accountBalanceRepository.find(userId, currency)
                .orElseGet(() -> accountBalanceRepository.save(new AccountCurrencyBalance(userId, currency, BigDecimal.ZERO)));
    }
}
