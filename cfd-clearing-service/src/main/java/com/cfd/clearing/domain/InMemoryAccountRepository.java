package com.cfd.clearing.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的账户仓储实现。
 *
 * <p>使用{@link ConcurrentHashMap}存储账户余额数据，主要用于单元测试场景。
 * 线程安全，支持并发访问。</p>
 *
 * @author CFD Platform Team
 */
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
