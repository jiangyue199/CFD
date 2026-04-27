package com.cfd.account.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.cfd.account.domain.AccountBalanceRepository;
import com.cfd.account.domain.AccountCurrencyBalance;

@Service
public class AccountApplicationService {

    private final AccountBalanceRepository accountBalanceRepository;

    public AccountApplicationService(AccountBalanceRepository accountBalanceRepository) {
        this.accountBalanceRepository = accountBalanceRepository;
    }

    public AccountCurrencyBalance deposit(String userId, String currency, BigDecimal amount) {
        AccountCurrencyBalance balance = accountBalanceRepository.find(userId, currency)
                .orElseGet(() -> accountBalanceRepository.save(new AccountCurrencyBalance(userId, currency, BigDecimal.ZERO)));
        balance.deposit(amount);
        accountBalanceRepository.save(balance);
        return balance;
    }

    public AccountCurrencyBalance withdraw(String userId, String currency, BigDecimal amount) {
        AccountCurrencyBalance balance = accountBalanceRepository.find(userId, currency)
                .orElseThrow(() -> new IllegalArgumentException("Account balance not found"));
        balance.withdraw(amount);
        accountBalanceRepository.save(balance);
        return balance;
    }

    public AccountCurrencyBalance query(String userId, String currency) {
        return accountBalanceRepository.find(userId, currency)
                .orElseGet(() -> accountBalanceRepository.save(new AccountCurrencyBalance(userId, currency, BigDecimal.ZERO)));
    }
}
