package com.cfd.account.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的账户余额仓储实现。
 *
 * <p>使用 {@link ConcurrentHashMap} 在内存中存储账户余额数据，
 * 适用于测试或无需持久化的场景。</p>
 *
 * @author CFD Platform Team
 */
public class InMemoryAccountBalanceRepository implements AccountBalanceRepository {

    /** 内存存储，键为 userId:currency 组合 */
    private final Map<String, AccountCurrencyBalance> balances = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountCurrencyBalance save(AccountCurrencyBalance balance) {
        balances.put(key(balance.getUserId(), balance.getCurrency()), balance);
        return balance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AccountCurrencyBalance> find(String userId, String currency) {
        return Optional.ofNullable(balances.get(key(userId, currency)));
    }

    /**
     * 生成内存存储键。
     *
     * @param userId   用户ID
     * @param currency 币种代码
     * @return 组合键字符串
     */
    private String key(String userId, String currency) {
        return userId + ':' + currency;
    }
}
