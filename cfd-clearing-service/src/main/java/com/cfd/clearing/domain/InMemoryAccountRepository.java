package com.cfd.clearing.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, AccountBalance> balances = new ConcurrentHashMap<>();

    @Override
    public AccountBalance saveIfAbsent(AccountBalance accountBalance) {
        return balances.computeIfAbsent(accountBalance.getUserId(), key -> accountBalance);
    }

    @Override
    public Optional<AccountBalance> findByUserId(String userId) {
        return Optional.ofNullable(balances.get(userId));
    }

    @Override
    public void update(AccountBalance accountBalance) {
        balances.put(accountBalance.getUserId(), accountBalance);
    }
}
