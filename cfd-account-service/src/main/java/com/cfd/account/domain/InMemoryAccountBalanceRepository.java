package com.cfd.account.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccountBalanceRepository implements AccountBalanceRepository {

    private final Map<String, AccountCurrencyBalance> balances = new ConcurrentHashMap<>();

    @Override
    public AccountCurrencyBalance save(AccountCurrencyBalance balance) {
        balances.put(key(balance.getUserId(), balance.getCurrency()), balance);
        return balance;
    }

    @Override
    public Optional<AccountCurrencyBalance> find(String userId, String currency) {
        return Optional.ofNullable(balances.get(key(userId, currency)));
    }

    private String key(String userId, String currency) {
        return userId + ':' + currency;
    }
}
